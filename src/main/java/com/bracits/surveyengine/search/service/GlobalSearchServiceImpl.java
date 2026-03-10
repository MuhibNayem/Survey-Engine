package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.search.config.SearchProperties;
import com.bracits.surveyengine.search.dto.GlobalSearchItemResponse;
import com.bracits.surveyengine.search.dto.GlobalSearchResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
@Slf4j
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private static final String SEARCH_SQL = """
            WITH input AS (
                SELECT
                    lower(trim(:query)) AS q_norm,
                    '%' || :queryLike || '%' AS q_like,
                    websearch_to_tsquery('english', :query) AS q_ts,
                    :tenantId::varchar AS tenant_id
            ),
            docs AS (
                SELECT
                    s.id::text AS id,
                    'survey'::text AS type,
                    s.title AS title,
                    COALESCE(s.description, '') AS body,
                    '/surveys'::text AS route,
                    setweight(to_tsvector('english', COALESCE(s.title, '')), 'A') ||
                    setweight(to_tsvector('english', COALESCE(s.description, '')), 'B') AS document,
                    lower(COALESCE(s.title, '')) AS title_norm,
                    lower(COALESCE(s.description, '')) AS body_norm
                FROM survey s
                JOIN input i ON s.tenant_id = i.tenant_id
                WHERE s.active = TRUE

                UNION ALL

                SELECT
                    c.id::text AS id,
                    'campaign'::text AS type,
                    c.name AS title,
                    COALESCE(c.description, '') AS body,
                    '/campaigns/' || c.id::text AS route,
                    setweight(to_tsvector('english', COALESCE(c.name, '')), 'A') ||
                    setweight(to_tsvector('english', COALESCE(c.description, '')), 'B') AS document,
                    lower(COALESCE(c.name, '')) AS title_norm,
                    lower(COALESCE(c.description, '')) AS body_norm
                FROM campaign c
                JOIN input i ON c.tenant_id = i.tenant_id
                WHERE c.active = TRUE

                UNION ALL

                SELECT
                    q.id::text AS id,
                    'question'::text AS type,
                    LEFT(q.text, 120) AS title,
                    q.text AS body,
                    '/questions'::text AS route,
                    setweight(to_tsvector('english', COALESCE(q.text, '')), 'A') AS document,
                    lower(COALESCE(q.text, '')) AS title_norm,
                    lower(COALESCE(q.text, '')) AS body_norm
                FROM question q
                JOIN input i ON q.tenant_id = i.tenant_id
                WHERE q.active = TRUE

                UNION ALL

                SELECT
                    c.id::text AS id,
                    'category'::text AS type,
                    c.name AS title,
                    COALESCE(c.description, '') AS body,
                    '/categories'::text AS route,
                    setweight(to_tsvector('english', COALESCE(c.name, '')), 'A') ||
                    setweight(to_tsvector('english', COALESCE(c.description, '')), 'B') AS document,
                    lower(COALESCE(c.name, '')) AS title_norm,
                    lower(COALESCE(c.description, '')) AS body_norm
                FROM category c
                JOIN input i ON c.tenant_id = i.tenant_id
                WHERE c.active = TRUE
            ),
            ranked AS (
                SELECT
                    d.id,
                    d.type,
                    d.title,
                    ts_headline(
                        'english',
                        d.title || ' ' || d.body,
                        i.q_ts,
                        'StartSel=[[[,StopSel=]]],MaxFragments=2,FragmentDelimiter= … ,MaxWords=16,MinWords=5,ShortWord=2'
                    ) AS snippet,
                    d.route,
                    CASE WHEN d.document @@ i.q_ts THEN 1 ELSE 0 END AS fts_match,
                    round((
                        ts_rank_cd(d.document, i.q_ts) * 2.0
                        + GREATEST(
                            similarity(d.title_norm, i.q_norm),
                            similarity(d.body_norm, i.q_norm),
                            word_similarity(i.q_norm, d.title_norm),
                            word_similarity(i.q_norm, d.body_norm)
                        )
                        + CASE WHEN d.title_norm LIKE i.q_like ESCAPE '\\' THEN 0.80 ELSE 0 END
                        + CASE WHEN d.body_norm LIKE i.q_like ESCAPE '\\' THEN 0.25 ELSE 0 END
                    )::numeric, 8) AS score
                FROM docs d
                CROSS JOIN input i
                WHERE
                    d.document @@ i.q_ts
                    OR similarity(d.title_norm, i.q_norm) >= :titleSimilarityThreshold
                    OR similarity(d.body_norm, i.q_norm) >= :bodySimilarityThreshold
                    OR word_similarity(i.q_norm, d.title_norm) >= :titleWordSimilarityThreshold
                    OR word_similarity(i.q_norm, d.body_norm) >= :bodyWordSimilarityThreshold
                    OR d.title_norm LIKE i.q_like ESCAPE '\\'
                    OR d.body_norm LIKE i.q_like ESCAPE '\\'
            )
            SELECT
                r.id,
                r.type,
                r.title,
                r.snippet,
                r.route,
                r.fts_match,
                r.score
            FROM ranked r
            WHERE
                :cursorPresent = FALSE
                OR (r.fts_match < :cursorFtsMatch)
                OR (r.fts_match = :cursorFtsMatch AND r.score < :cursorScore)
                OR (r.fts_match = :cursorFtsMatch AND r.score = :cursorScore AND r.title > :cursorTitle)
                OR (r.fts_match = :cursorFtsMatch AND r.score = :cursorScore AND r.title = :cursorTitle AND r.type > :cursorType)
                OR (r.fts_match = :cursorFtsMatch AND r.score = :cursorScore AND r.title = :cursorTitle AND r.type = :cursorType AND r.id > :cursorId)
            ORDER BY
                r.fts_match DESC,
                r.score DESC,
                r.title ASC,
                r.type ASC,
                r.id ASC
            LIMIT :limitPlusOne
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SearchProperties properties;
    private final CircuitBreaker circuitBreaker;
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Counter rateLimitedCounter;
    private final Timer searchLatencyTimer;

    private final Map<String, RateLimitWindow> rateLimitWindows = new ConcurrentHashMap<>();

    public GlobalSearchServiceImpl(
            NamedParameterJdbcTemplate jdbcTemplate,
            SearchProperties properties,
            MeterRegistry meterRegistry) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.circuitBreaker = CircuitBreaker.of("globalSearch", CircuitBreakerConfig.custom()
                .failureRateThreshold(properties.getCircuitBreakerFailureRateThreshold())
                .slidingWindowSize(properties.getCircuitBreakerSlidingWindowSize())
                .minimumNumberOfCalls(properties.getCircuitBreakerMinimumCalls())
                .waitDurationInOpenState(Duration.ofSeconds(properties.getCircuitBreakerOpenStateSeconds()))
                .permittedNumberOfCallsInHalfOpenState(properties.getCircuitBreakerHalfOpenCalls())
                .recordException(throwable -> true)
                .build());
        this.requestCounter = meterRegistry.counter("surveyengine.search.requests");
        this.errorCounter = meterRegistry.counter("surveyengine.search.errors");
        this.rateLimitedCounter = meterRegistry.counter("surveyengine.search.rate_limited");
        this.searchLatencyTimer = meterRegistry.timer("surveyengine.search.latency");
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalSearchResponse search(String query, int limit, String cursor) {
        String tenantId = TenantContext.getTenantId();
        String userId = TenantContext.getUserId() == null ? "anonymous" : TenantContext.getUserId();
        String normalized = normalizeQuery(query);

        if (tenantId == null || normalized.length() < properties.getMinQueryLength()) {
            return new GlobalSearchResponse(normalized, 0, List.of(), null);
        }
        if (normalized.length() > properties.getMaxQueryLength()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "Search query cannot exceed " + properties.getMaxQueryLength() + " characters");
        }

        requestCounter.increment();
        enforceRateLimit(tenantId, userId);

        int requestedLimit = limit > 0 ? limit : properties.getDefaultLimit();
        int cappedLimit = Math.min(Math.max(requestedLimit, 1), properties.getMaxResults());

        SearchCursor decodedCursor = decodeCursor(cursor);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("query", normalized)
                .addValue("queryLike", escapeLikePattern(normalized.toLowerCase(Locale.ROOT)))
                .addValue("tenantId", tenantId)
                .addValue("titleSimilarityThreshold", properties.getTitleSimilarityThreshold())
                .addValue("bodySimilarityThreshold", properties.getBodySimilarityThreshold())
                .addValue("titleWordSimilarityThreshold", properties.getTitleWordSimilarityThreshold())
                .addValue("bodyWordSimilarityThreshold", properties.getBodyWordSimilarityThreshold())
                .addValue("limitPlusOne", cappedLimit + 1)
                .addValue("cursorPresent", decodedCursor != null)
                .addValue("cursorFtsMatch", decodedCursor != null ? decodedCursor.ftsMatch() : 0)
                .addValue("cursorScore", decodedCursor != null ? decodedCursor.score() : BigDecimal.ZERO)
                .addValue("cursorTitle", decodedCursor != null ? decodedCursor.title() : "")
                .addValue("cursorType", decodedCursor != null ? decodedCursor.type() : "")
                .addValue("cursorId", decodedCursor != null ? decodedCursor.id() : "");

        long started = System.nanoTime();
        Supplier<List<SearchRow>> supplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> executeSearchQuery(params));
        try {
            List<SearchRow> rows = supplier.get();
            boolean hasMore = rows.size() > cappedLimit;
            List<SearchRow> pageRows = hasMore ? rows.subList(0, cappedLimit) : rows;
            List<GlobalSearchItemResponse> items = pageRows.stream()
                    .map(row -> new GlobalSearchItemResponse(
                            row.id(),
                            row.type(),
                            row.title(),
                            row.snippet(),
                            row.route(),
                            row.score().doubleValue()))
                    .toList();

            String nextCursor = hasMore && !pageRows.isEmpty()
                    ? encodeCursor(pageRows.get(pageRows.size() - 1))
                    : null;

            long elapsedNanos = System.nanoTime() - started;
            searchLatencyTimer.record(Duration.ofNanos(elapsedNanos));
            long elapsedMs = Duration.ofNanos(elapsedNanos).toMillis();
            if (elapsedMs >= properties.getSlowQueryThresholdMs()) {
                log.warn("Slow global search query detected: {} ms, tenant={}, query='{}'",
                        elapsedMs, tenantId, normalized);
            }
            return new GlobalSearchResponse(normalized, items.size(), items, nextCursor);
        } catch (CallNotPermittedException ex) {
            errorCounter.increment();
            log.error("Global search blocked by circuit breaker for query='{}'", normalized, ex);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Global search is temporarily unavailable");
        } catch (DataAccessException ex) {
            errorCounter.increment();
            log.error("Global search failed due to database error for query='{}'", normalized, ex);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Global search is temporarily unavailable");
        } catch (RuntimeException ex) {
            errorCounter.increment();
            log.error("Global search failed unexpectedly for query='{}'", normalized, ex);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Global search is temporarily unavailable");
        }
    }

    private List<SearchRow> executeSearchQuery(MapSqlParameterSource params) {
        JdbcTemplate classicJdbc = jdbcTemplate.getJdbcTemplate();
        classicJdbc.execute("SET LOCAL statement_timeout = " + properties.getQueryTimeoutMs());
        return jdbcTemplate.query(
                SEARCH_SQL,
                params,
                (rs, rowNum) -> new SearchRow(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("snippet"),
                        rs.getString("route"),
                        rs.getInt("fts_match"),
                        rs.getBigDecimal("score")));
    }

    private SearchCursor decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", -1);
            if (parts.length != 5) {
                throw new IllegalArgumentException("Invalid cursor format");
            }
            return new SearchCursor(
                    Integer.parseInt(parts[0]),
                    new BigDecimal(parts[1]),
                    urlDecode(parts[2]),
                    urlDecode(parts[3]),
                    urlDecode(parts[4]));
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid search cursor");
        }
    }

    private String encodeCursor(SearchRow row) {
        String raw = row.ftsMatch()
                + "|" + row.score().toPlainString()
                + "|" + urlEncode(row.title())
                + "|" + urlEncode(row.type())
                + "|" + urlEncode(row.id());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String urlEncode(String value) {
        return java.net.URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String urlDecode(String value) {
        return java.net.URLDecoder.decode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private void enforceRateLimit(String tenantId, String userId) {
        long minuteBucket = System.currentTimeMillis() / 60000L;
        String key = tenantId + "|" + userId;
        RateLimitWindow updated = rateLimitWindows.compute(key, (k, existing) -> {
            if (existing == null || existing.minuteBucket() != minuteBucket) {
                return new RateLimitWindow(minuteBucket, 1);
            }
            return new RateLimitWindow(existing.minuteBucket(), existing.count() + 1);
        });
        cleanupRateLimitWindows(minuteBucket);
        if (updated != null && updated.count() > properties.getRateLimitPerMinute()) {
            rateLimitedCounter.increment();
            throw new BusinessException(
                    ErrorCode.QUOTA_EXCEEDED,
                    "Search rate limit exceeded. Please retry in a minute.");
        }
    }

    private void cleanupRateLimitWindows(long currentMinuteBucket) {
        if (rateLimitWindows.size() < 4096) {
            return;
        }
        rateLimitWindows.entrySet()
                .removeIf(entry -> entry.getValue().minuteBucket() < currentMinuteBucket - 1);
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            return "";
        }
        String noControlChars = query.replaceAll("\\p{Cntrl}", " ");
        return noControlChars.trim().replaceAll("\\s{2,}", " ");
    }

    private String escapeLikePattern(String query) {
        return query
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private record SearchCursor(
            int ftsMatch,
            BigDecimal score,
            String title,
            String type,
            String id) {
    }

    private record SearchRow(
            String id,
            String type,
            String title,
            String snippet,
            String route,
            int ftsMatch,
            BigDecimal score) {
    }

    private record RateLimitWindow(long minuteBucket, int count) {
    }
}
