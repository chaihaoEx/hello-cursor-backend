package com.kinkle.helloquick.common.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisMonitoringService单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Redis监控服务测试")
class RedisMonitoringServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisScript<String> cacheStatsScript;

    @Mock
    private RedisScript<Long> cleanupExpiredCacheScript;

    @Mock
    private RedisScript<Long> batchExpireScript;

    private RedisMonitoringService redisMonitoringService;

    @BeforeEach
    void setUp() {
        redisMonitoringService = new RedisMonitoringService(
            redisTemplate, 
            cacheStatsScript, 
            cleanupExpiredCacheScript, 
            batchExpireScript
        );
    }

    @Nested
    @DisplayName("缓存统计信息测试")
    class CacheStatsTest {

        @Test
        @DisplayName("应该成功获取缓存统计信息")
        void shouldGetCacheStatsSuccessfully() {
            // Given
            String pattern = "test:*";
            String scriptResult = "{\"count\": 10, \"memory\": 1024}";
            when(redisTemplate.execute(eq(cacheStatsScript), anyList(), eq(pattern)))
                .thenReturn(scriptResult);

            // When
            Map<String, Object> result = redisMonitoringService.getCacheStats(pattern);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("pattern")).isEqualTo(pattern);
            assertThat(result.get("rawResult")).isEqualTo(scriptResult);
            verify(redisTemplate).execute(eq(cacheStatsScript), anyList(), eq(pattern));
        }

        @Test
        @DisplayName("应该处理脚本执行返回null的情况")
        void shouldHandleNullScriptResult() {
            // Given
            String pattern = "test:*";
            when(redisTemplate.execute(eq(cacheStatsScript), anyList(), eq(pattern)))
                .thenReturn(null);

            // When
            Map<String, Object> result = redisMonitoringService.getCacheStats(pattern);

            // Then
            assertThat(result).containsKey("error");
            assertThat(result.get("error")).isEqualTo("Failed to get cache stats");
        }

        @Test
        @DisplayName("应该处理脚本执行异常")
        void shouldHandleScriptExecutionException() {
            // Given
            String pattern = "test:*";
            when(redisTemplate.execute(eq(cacheStatsScript), anyList(), eq(pattern)))
                .thenThrow(new RuntimeException("Redis连接失败"));

            // When
            Map<String, Object> result = redisMonitoringService.getCacheStats(pattern);

            // Then
            assertThat(result).containsKey("error");
            assertThat(result.get("error")).isEqualTo("Failed to get cache stats");
        }
    }

    @Nested
    @DisplayName("缓存键操作测试")
    class CacheKeysTest {

        @Test
        @DisplayName("应该成功获取所有缓存键")
        void shouldGetAllKeysSuccessfully() {
            // Given
            String pattern = "user:*";
            Set<String> expectedKeys = Set.of("user:1", "user:2", "user:3");
            when(redisTemplate.keys(pattern)).thenReturn(expectedKeys);

            // When
            Set<String> result = redisMonitoringService.getAllKeys(pattern);

            // Then
            assertThat(result).isEqualTo(expectedKeys);
            verify(redisTemplate).keys(pattern);
        }

        @Test
        @DisplayName("应该处理获取键时的异常")
        void shouldHandleGetKeysException() {
            // Given
            String pattern = "user:*";
            when(redisTemplate.keys(pattern)).thenThrow(new RuntimeException("Redis连接失败"));

            // When
            Set<String> result = redisMonitoringService.getAllKeys(pattern);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该成功获取缓存键数量")
        void shouldGetKeyCountSuccessfully() {
            // Given
            String pattern = "session:*";
            Set<String> keys = Set.of("session:1", "session:2");
            when(redisTemplate.keys(pattern)).thenReturn(keys);

            // When
            long result = redisMonitoringService.getKeyCount(pattern);

            // Then
            assertThat(result).isEqualTo(2);
        }

        @Test
        @DisplayName("应该处理获取键数量时返回null")
        void shouldHandleNullKeysWhenGettingCount() {
            // Given
            String pattern = "session:*";
            when(redisTemplate.keys(pattern)).thenReturn(null);

            // When
            long result = redisMonitoringService.getKeyCount(pattern);

            // Then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理获取键数量时的异常")
        void shouldHandleGetKeyCountException() {
            // Given
            String pattern = "session:*";
            when(redisTemplate.keys(pattern)).thenThrow(new RuntimeException("Redis连接失败"));

            // When
            long result = redisMonitoringService.getKeyCount(pattern);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("缓存清理测试")
    class CacheCleanupTest {

        @Test
        @DisplayName("应该成功清理过期缓存")
        void shouldCleanupExpiredCacheSuccessfully() {
            // Given
            String pattern = "temp:*";
            Long cleanupCount = 5L;
            when(redisTemplate.execute(eq(cleanupExpiredCacheScript), anyList(), eq(pattern)))
                .thenReturn(cleanupCount);

            // When
            long result = redisMonitoringService.cleanupExpiredCache(pattern);

            // Then
            assertThat(result).isEqualTo(5);
            verify(redisTemplate).execute(eq(cleanupExpiredCacheScript), anyList(), eq(pattern));
        }

        @Test
        @DisplayName("应该处理清理脚本返回null")
        void shouldHandleNullCleanupResult() {
            // Given
            String pattern = "temp:*";
            when(redisTemplate.execute(eq(cleanupExpiredCacheScript), anyList(), eq(pattern)))
                .thenReturn(null);

            // When
            long result = redisMonitoringService.cleanupExpiredCache(pattern);

            // Then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理清理脚本执行异常")
        void shouldHandleCleanupScriptException() {
            // Given
            String pattern = "temp:*";
            when(redisTemplate.execute(eq(cleanupExpiredCacheScript), anyList(), eq(pattern)))
                .thenThrow(new RuntimeException("清理失败"));

            // When
            long result = redisMonitoringService.cleanupExpiredCache(pattern);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("批量过期测试")
    class BatchExpireTest {

        @Test
        @DisplayName("应该成功批量设置过期时间")
        void shouldBatchExpireSuccessfully() {
            // Given
            String pattern = "cache:*";
            long expireTime = 3600L;
            Long updatedCount = 10L;
            when(redisTemplate.execute(eq(batchExpireScript), anyList(), eq(pattern), eq("3600")))
                .thenReturn(updatedCount);

            // When
            long result = redisMonitoringService.batchExpire(pattern, expireTime);

            // Then
            assertThat(result).isEqualTo(10L);
            verify(redisTemplate).execute(eq(batchExpireScript), anyList(), eq(pattern), eq("3600"));
        }

        @Test
        @DisplayName("应该处理批量过期脚本返回null")
        void shouldHandleNullBatchExpireResult() {
            // Given
            String pattern = "cache:*";
            long expireTime = 1800L;
            when(redisTemplate.execute(eq(batchExpireScript), anyList(), eq(pattern), eq("1800")))
                .thenReturn(null);

            // When
            long result = redisMonitoringService.batchExpire(pattern, expireTime);

            // Then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理批量过期脚本执行异常")
        void shouldHandleBatchExpireScriptException() {
            // Given
            String pattern = "cache:*";
            long expireTime = 900L;
            when(redisTemplate.execute(eq(batchExpireScript), anyList(), eq(pattern), eq("900")))
                .thenThrow(new RuntimeException("批量过期失败"));

            // When
            long result = redisMonitoringService.batchExpire(pattern, expireTime);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("内存信息测试")
    class MemoryInfoTest {

        @Test
        @DisplayName("应该成功获取内存信息")
        void shouldGetMemoryInfoSuccessfully() {
            // Given
            Set<String> allKeys = Set.of(
                "hello-quick:user:1",
                "hello-quick:user:2",
                "hello-quick:session:abc",
                "hello-quick:config:app"
            );
            when(redisTemplate.keys("*")).thenReturn(allKeys);

            // When
            Map<String, Object> result = redisMonitoringService.getMemoryInfo();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("totalKeys")).isEqualTo(4L);
            assertThat(result.get("timestamp")).isNotNull();
            
            @SuppressWarnings("unchecked")
            Map<String, Long> namespaceStats = (Map<String, Long>) result.get("namespaceStats");
            assertThat(namespaceStats).containsEntry("user", 2L);
            assertThat(namespaceStats).containsEntry("session", 1L);
            assertThat(namespaceStats).containsEntry("config", 1L);
        }

        @Test
        @DisplayName("应该处理获取内存信息时返回null键")
        void shouldHandleNullKeysWhenGettingMemoryInfo() {
            // Given
            when(redisTemplate.keys("*")).thenReturn(null);

            // When
            Map<String, Object> result = redisMonitoringService.getMemoryInfo();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("totalKeys")).isEqualTo(0L);
            assertThat(result.get("namespaceStats")).isNotNull();
        }

        @Test
        @DisplayName("应该处理获取内存信息时的异常")
        void shouldHandleGetMemoryInfoException() {
            // Given
            when(redisTemplate.keys("*")).thenThrow(new RuntimeException("Redis连接失败"));

            // When
            Map<String, Object> result = redisMonitoringService.getMemoryInfo();

            // Then
            assertThat(result).containsKey("error");
            assertThat(result.get("error")).isEqualTo("Failed to get memory info");
        }

        @Test
        @DisplayName("应该处理无效的缓存键")
        void shouldHandleInvalidCacheKeys() {
            // Given
            Set<String> allKeys = Set.of(
                "hello-quick:user:1",
                "invalid-key",
                "hello-quick:session:abc"
            );
            when(redisTemplate.keys("*")).thenReturn(allKeys);

            // When
            Map<String, Object> result = redisMonitoringService.getMemoryInfo();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("totalKeys")).isEqualTo(3L);
            
            @SuppressWarnings("unchecked")
            Map<String, Long> namespaceStats = (Map<String, Long>) result.get("namespaceStats");
            assertThat(namespaceStats).containsEntry("user", 1L);
            assertThat(namespaceStats).containsEntry("session", 1L);
        }
    }

    @Nested
    @DisplayName("性能指标测试")
    class PerformanceMetricsTest {

        @Test
        @DisplayName("应该成功获取性能指标")
        void shouldGetPerformanceMetricsSuccessfully() {
            // Given
            @SuppressWarnings("unchecked")
            ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            doNothing().when(valueOps).set(anyString(), anyString(), any(Duration.class));
            // 让get方法返回与set方法相同的值，这样testValue.equals(checkValue)会返回true
            when(valueOps.get(anyString())).thenAnswer(invocation -> {
                // 返回一个以"performance-test-"开头的值，这样testValue.equals(checkValue)会返回true
                return "performance-test-" + System.currentTimeMillis();
            });
            when(redisTemplate.delete(anyString())).thenReturn(true);

            // When
            Map<String, Object> result = redisMonitoringService.getPerformanceMetrics();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("setSuccess")).isEqualTo(true);
            assertThat(result.get("getSuccess")).isEqualTo(true);
            assertThat(result.get("setOperationTime")).isNotNull();
            assertThat(result.get("getOperationTime")).isNotNull();
            assertThat(result.get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("应该处理设置操作失败的情况")
        void shouldHandleSetOperationFailure() {
            // Given
            @SuppressWarnings("unchecked")
            ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            doThrow(new RuntimeException("Set operation failed")).when(valueOps).set(anyString(), anyString(), any(Duration.class));

            // When
            Map<String, Object> result = redisMonitoringService.getPerformanceMetrics();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("setSuccess")).isEqualTo(false);
            assertThat(result.get("getSuccess")).isEqualTo(false);
            assertThat(result.get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("应该处理获取性能指标时的异常")
        void shouldHandleGetPerformanceMetricsException() {
            // Given
            when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis连接失败"));

            // When
            Map<String, Object> result = redisMonitoringService.getPerformanceMetrics();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("setSuccess")).isEqualTo(false);
            assertThat(result.get("getSuccess")).isEqualTo(false);
            assertThat(result.get("timestamp")).isNotNull();
        }
    }

    @Nested
    @DisplayName("缓存概览测试")
    class CacheOverviewTest {

        @Test
        @DisplayName("应该成功获取缓存概览")
        void shouldGetCacheOverviewSuccessfully() {
            // Given
            when(redisTemplate.keys(anyString())).thenReturn(Set.of("key1", "key2"));

            // When
            Map<String, Object> result = redisMonitoringService.getCacheOverview();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("totalKeys")).isEqualTo(16L); // 8个命名空间 * 2个键
            assertThat(result.get("timestamp")).isNotNull();
            
            @SuppressWarnings("unchecked")
            Map<String, Long> namespaceCounts = (Map<String, Long>) result.get("namespaceCounts");
            assertThat(namespaceCounts).hasSize(8);
            assertThat(namespaceCounts.values()).allMatch(count -> count == 2L);
        }

        @Test
        @DisplayName("应该处理某些命名空间没有键的情况")
        void shouldHandleEmptyNamespaces() {
            // Given
            when(redisTemplate.keys(anyString())).thenReturn(Set.of());

            // When
            Map<String, Object> result = redisMonitoringService.getCacheOverview();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("totalKeys")).isEqualTo(0L);
            
            @SuppressWarnings("unchecked")
            Map<String, Long> namespaceCounts = (Map<String, Long>) result.get("namespaceCounts");
            assertThat(namespaceCounts).isEmpty();
        }

        @Test
        @DisplayName("应该处理获取缓存概览时的异常")
        void shouldHandleGetCacheOverviewException() {
            // Given
            when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis连接失败"));

            // When
            Map<String, Object> result = redisMonitoringService.getCacheOverview();

            // Then
            assertThat(result).containsKey("error");
            assertThat(result.get("error")).isEqualTo("获取缓存概览失败");
        }

        @Test
        @DisplayName("应该处理部分命名空间查询失败")
        void shouldHandlePartialNamespaceQueryFailure() {
            // Given
            when(redisTemplate.keys(anyString()))
                .thenThrow(new RuntimeException("部分查询失败"));

            // When
            Map<String, Object> result = redisMonitoringService.getCacheOverview();

            // Then
            assertThat(result).containsKey("error");
            assertThat(result.get("error")).isEqualTo("获取缓存概览失败");
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTest {

        @Test
        @DisplayName("应该处理空字符串模式")
        void shouldHandleEmptyPattern() {
            // Given
            when(redisTemplate.keys("")).thenReturn(Set.of());

            // When
            Set<String> result = redisMonitoringService.getAllKeys("");

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理null模式")
        @SuppressWarnings("null")
        void shouldHandleNullPattern() {
            // Given
            String nullPattern = null;
            when(redisTemplate.keys(nullPattern)).thenThrow(new IllegalArgumentException("Pattern不能为null"));

            // When
            Set<String> result = redisMonitoringService.getAllKeys(nullPattern);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理负数过期时间")
        void shouldHandleNegativeExpireTime() {
            // Given
            String pattern = "test:*";
            long negativeExpireTime = -1L;
            when(redisTemplate.execute(eq(batchExpireScript), anyList(), eq(pattern), eq("-1")))
                .thenReturn(0L);

            // When
            long result = redisMonitoringService.batchExpire(pattern, negativeExpireTime);

            // Then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理零过期时间")
        void shouldHandleZeroExpireTime() {
            // Given
            String pattern = "test:*";
            long zeroExpireTime = 0L;
            when(redisTemplate.execute(eq(batchExpireScript), anyList(), eq(pattern), eq("0")))
                .thenReturn(0L);

            // When
            long result = redisMonitoringService.batchExpire(pattern, zeroExpireTime);

            // Then
            assertThat(result).isEqualTo(0);
        }
    }
}
