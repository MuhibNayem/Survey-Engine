package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.search.config.SearchProperties;
import com.bracits.surveyengine.search.dto.GlobalSearchResponse;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalSearchServiceImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Mock
    private JdbcTemplate classicJdbcTemplate;

    private SearchProperties properties;
    private GlobalSearchServiceImpl service;

    @BeforeEach
    void setUp() {
        properties = new SearchProperties();
        properties.setRateLimitPerMinute(10);
        properties.setCacheTtlSeconds(60);
        properties.setFallbackCacheGraceSeconds(300);
        properties.setCacheMaxEntries(100);
        properties.setQueryTimeoutMs(3000);
        service = new GlobalSearchServiceImpl(jdbcTemplate, properties, new SimpleMeterRegistry());
        TenantContext.set(new TenantContext.TenantInfo(
                "tenant-a",
                "user-1",
                "user@example.com",
                Set.of("ADMIN")));
        lenient().when(jdbcTemplate.getJdbcTemplate()).thenReturn(classicJdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldReturnEmptyForShortQueryWithoutDatabaseHit() {
        GlobalSearchResponse response = service.search("a", 10);
        assertThat(response.total()).isEqualTo(0);
        verify(jdbcTemplate, times(0)).query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    void shouldRejectQueryAboveMaxLength() {
        properties.setMaxQueryLength(5);
        assertThatThrownBy(() -> service.search("this is too long", 10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot exceed 5 characters");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldEscapeLikeWildcardsInQueryParams() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        service.search("100%_match\\test", 10);

        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), captor.capture(), any(RowMapper.class));
        assertThat(captor.getValue().getValue("queryLike")).isEqualTo("100\\%\\_match\\\\test");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRateLimitPerTenantUser() {
        properties.setRateLimitPerMinute(1);
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        service.search("hello", 10);

        assertThatThrownBy(() -> service.search("hello-world", 10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("rate limit exceeded");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldApplyStatementTimeoutBeforeQuery() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        service.search("timeout", 10);

        verify(classicJdbcTemplate).execute("SET LOCAL statement_timeout = 3000");
    }

}
