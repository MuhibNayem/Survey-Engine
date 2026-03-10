package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.search.dto.GlobalSearchItemResponse;
import com.bracits.surveyengine.search.dto.GlobalSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private static final String SEARCH_SQL = """
            WITH input AS (
                SELECT
                    lower(trim(:query)) AS q_norm,
                    '%' || lower(trim(:query)) || '%' AS q_like,
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
            )
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
                (
                    ts_rank_cd(d.document, i.q_ts) * 2.0
                    + GREATEST(
                        similarity(d.title_norm, i.q_norm),
                        similarity(d.body_norm, i.q_norm),
                        word_similarity(i.q_norm, d.title_norm),
                        word_similarity(i.q_norm, d.body_norm)
                    )
                    + CASE WHEN d.title_norm LIKE i.q_like THEN 0.80 ELSE 0 END
                    + CASE WHEN d.body_norm LIKE i.q_like THEN 0.25 ELSE 0 END
                ) AS score
            FROM docs d
            CROSS JOIN input i
            WHERE
                d.document @@ i.q_ts
                OR similarity(d.title_norm, i.q_norm) >= 0.24
                OR similarity(d.body_norm, i.q_norm) >= 0.18
                OR word_similarity(i.q_norm, d.title_norm) >= 0.45
                OR word_similarity(i.q_norm, d.body_norm) >= 0.35
                OR d.title_norm LIKE i.q_like
                OR d.body_norm LIKE i.q_like
            ORDER BY
                CASE WHEN d.document @@ i.q_ts THEN 1 ELSE 0 END DESC,
                score DESC,
                d.title ASC
            LIMIT :limit
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public GlobalSearchResponse search(String query, int limit) {
        String tenantId = TenantContext.getTenantId();
        String normalized = query == null ? "" : query.trim();
        int cappedLimit = Math.min(Math.max(limit, 1), 30);

        if (tenantId == null || normalized.length() < 2) {
            return new GlobalSearchResponse(normalized, 0, List.of());
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("query", normalized)
                .addValue("tenantId", tenantId)
                .addValue("limit", cappedLimit);

        List<GlobalSearchItemResponse> items = jdbcTemplate.query(
                SEARCH_SQL,
                params,
                (rs, rowNum) -> new GlobalSearchItemResponse(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("snippet"),
                        rs.getString("route"),
                        rs.getDouble("score")));

        return new GlobalSearchResponse(normalized, items.size(), items);
    }
}
