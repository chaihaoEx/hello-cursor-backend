package com.kinkle.helloquick.controller;

import com.kinkle.helloquick.common.result.ResultCode;
import com.kinkle.helloquick.common.service.RedisMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RedisMonitoringController 单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Redis监控控制器测试")
class RedisMonitoringControllerTest {

    @Mock
    private RedisMonitoringService redisMonitoringService;

    private RedisMonitoringController redisMonitoringController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        redisMonitoringController = new RedisMonitoringController(redisMonitoringService);
        mockMvc = MockMvcBuilders.standaloneSetup(redisMonitoringController).build();
    }

    @Nested
    @DisplayName("缓存概览测试")
    class CacheOverviewTest {

        @Test
        @DisplayName("应该成功获取缓存概览")
        void shouldGetCacheOverviewSuccessfully() throws Exception {
            // Given
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalKeys", 100L);
            overview.put("namespaceCounts", Map.of("user", 50L, "session", 30L));
            when(redisMonitoringService.getCacheOverview()).thenReturn(overview);

            // When & Then
            mockMvc.perform(get("/api/redis/overview"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data.totalKeys").value(100))
                    .andExpect(jsonPath("$.data.namespaceCounts.user").value(50))
                    .andExpect(jsonPath("$.data.namespaceCounts.session").value(30));

            verify(redisMonitoringService).getCacheOverview();
        }

        @Test
        @DisplayName("应该处理获取缓存概览异常")
        void shouldHandleGetCacheOverviewException() throws Exception {
            // Given
            when(redisMonitoringService.getCacheOverview())
                    .thenThrow(new RuntimeException("Redis连接失败"));

            // When & Then
            mockMvc.perform(get("/api/redis/overview"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取缓存概览失败")));
        }
    }

    @Nested
    @DisplayName("缓存统计测试")
    class CacheStatsTest {

        @Test
        @DisplayName("应该成功获取缓存统计信息")
        void shouldGetCacheStatsSuccessfully() throws Exception {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalKeys", 50L);
            stats.put("memoryUsage", "10MB");
            when(redisMonitoringService.getCacheStats(anyString())).thenReturn(stats);

            // When & Then
            mockMvc.perform(get("/api/redis/stats")
                            .param("pattern", "hello-quick:user:*"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data.totalKeys").value(50))
                    .andExpect(jsonPath("$.data.memoryUsage").value("10MB"));

            verify(redisMonitoringService).getCacheStats("hello-quick:user:*");
        }

        @Test
        @DisplayName("应该使用默认模式获取缓存统计信息")
        void shouldGetCacheStatsWithDefaultPattern() throws Exception {
            // Given
            Map<String, Object> stats = new HashMap<>();
            when(redisMonitoringService.getCacheStats(anyString())).thenReturn(stats);

            // When & Then
            mockMvc.perform(get("/api/redis/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisMonitoringService).getCacheStats("hello-quick:*");
        }

        @Test
        @DisplayName("应该处理获取缓存统计信息异常")
        void shouldHandleGetCacheStatsException() throws Exception {
            // Given
            when(redisMonitoringService.getCacheStats(anyString()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/redis/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取缓存统计失败")));
        }
    }

    @Nested
    @DisplayName("缓存键测试")
    class CacheKeysTest {

        @Test
        @DisplayName("应该成功获取所有缓存键")
        void shouldGetAllKeysSuccessfully() throws Exception {
            // Given
            Set<String> keys = Set.of("hello-quick:user:1", "hello-quick:user:2");
            when(redisMonitoringService.getAllKeys(anyString())).thenReturn(keys);

            // When & Then
            mockMvc.perform(get("/api/redis/keys")
                            .param("pattern", "hello-quick:user:*"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(redisMonitoringService).getAllKeys("hello-quick:user:*");
        }

        @Test
        @DisplayName("应该使用默认模式获取所有缓存键")
        void shouldGetAllKeysWithDefaultPattern() throws Exception {
            // Given
            Set<String> keys = Set.of();
            when(redisMonitoringService.getAllKeys(anyString())).thenReturn(keys);

            // When & Then
            mockMvc.perform(get("/api/redis/keys"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisMonitoringService).getAllKeys("hello-quick:*");
        }

        @Test
        @DisplayName("应该处理获取缓存键异常")
        void shouldHandleGetAllKeysException() throws Exception {
            // Given
            when(redisMonitoringService.getAllKeys(anyString()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/redis/keys"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取缓存键失败")));
        }
    }

    @Nested
    @DisplayName("键数量测试")
    class KeyCountTest {

        @Test
        @DisplayName("应该成功获取键数量")
        void shouldGetKeyCountSuccessfully() throws Exception {
            // Given
            when(redisMonitoringService.getKeyCount(anyString())).thenReturn(100L);

            // When & Then
            mockMvc.perform(get("/api/redis/count")
                            .param("pattern", "hello-quick:user:*"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data").value(100));

            verify(redisMonitoringService).getKeyCount("hello-quick:user:*");
        }

        @Test
        @DisplayName("应该使用默认模式获取键数量")
        void shouldGetKeyCountWithDefaultPattern() throws Exception {
            // Given
            when(redisMonitoringService.getKeyCount(anyString())).thenReturn(0L);

            // When & Then
            mockMvc.perform(get("/api/redis/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisMonitoringService).getKeyCount("hello-quick:*");
        }

        @Test
        @DisplayName("应该处理获取键数量异常")
        void shouldHandleGetKeyCountException() throws Exception {
            // Given
            when(redisMonitoringService.getKeyCount(anyString()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/redis/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取键数量失败")));
        }
    }

    @Nested
    @DisplayName("内存信息测试")
    class MemoryInfoTest {

        @Test
        @DisplayName("应该成功获取内存信息")
        void shouldGetMemoryInfoSuccessfully() throws Exception {
            // Given
            Map<String, Object> memoryInfo = new HashMap<>();
            memoryInfo.put("usedMemory", "10MB");
            memoryInfo.put("maxMemory", "100MB");
            when(redisMonitoringService.getMemoryInfo()).thenReturn(memoryInfo);

            // When & Then
            mockMvc.perform(get("/api/redis/memory"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data.usedMemory").value("10MB"))
                    .andExpect(jsonPath("$.data.maxMemory").value("100MB"));

            verify(redisMonitoringService).getMemoryInfo();
        }

        @Test
        @DisplayName("应该处理获取内存信息异常")
        void shouldHandleGetMemoryInfoException() throws Exception {
            // Given
            when(redisMonitoringService.getMemoryInfo())
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/redis/memory"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取内存信息失败")));
        }
    }

    @Nested
    @DisplayName("性能指标测试")
    class PerformanceMetricsTest {

        @Test
        @DisplayName("应该成功获取性能指标")
        void shouldGetPerformanceMetricsSuccessfully() throws Exception {
            // Given
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("setOperationTime", 1.5);
            metrics.put("getOperationTime", 0.8);
            metrics.put("setSuccess", true);
            metrics.put("getSuccess", true);
            when(redisMonitoringService.getPerformanceMetrics()).thenReturn(metrics);

            // When & Then
            mockMvc.perform(get("/api/redis/performance"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data.setOperationTime").value(1.5))
                    .andExpect(jsonPath("$.data.getOperationTime").value(0.8))
                    .andExpect(jsonPath("$.data.setSuccess").value(true))
                    .andExpect(jsonPath("$.data.getSuccess").value(true));

            verify(redisMonitoringService).getPerformanceMetrics();
        }

        @Test
        @DisplayName("应该处理获取性能指标异常")
        void shouldHandleGetPerformanceMetricsException() throws Exception {
            // Given
            when(redisMonitoringService.getPerformanceMetrics())
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/redis/performance"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取性能指标失败")));
        }
    }

    @Nested
    @DisplayName("清理过期缓存测试")
    class CleanupExpiredCacheTest {

        @Test
        @DisplayName("应该成功清理过期缓存")
        void shouldCleanupExpiredCacheSuccessfully() throws Exception {
            // Given
            when(redisMonitoringService.cleanupExpiredCache(anyString())).thenReturn(10L);

            // When & Then
            mockMvc.perform(post("/api/redis/cleanup")
                            .param("pattern", "hello-quick:user:*"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data").value(10))
                    .andExpect(jsonPath("$.message").value("清理了 10 个过期缓存"));

            verify(redisMonitoringService).cleanupExpiredCache("hello-quick:user:*");
        }

        @Test
        @DisplayName("应该使用默认模式清理过期缓存")
        void shouldCleanupExpiredCacheWithDefaultPattern() throws Exception {
            // Given
            when(redisMonitoringService.cleanupExpiredCache(anyString())).thenReturn(0L);

            // When & Then
            mockMvc.perform(post("/api/redis/cleanup"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisMonitoringService).cleanupExpiredCache("hello-quick:*");
        }

        @Test
        @DisplayName("应该处理清理过期缓存异常")
        void shouldHandleCleanupExpiredCacheException() throws Exception {
            // Given
            when(redisMonitoringService.cleanupExpiredCache(anyString()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(post("/api/redis/cleanup"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("清理过期缓存失败")));
        }
    }

    @Nested
    @DisplayName("批量设置过期时间测试")
    class BatchExpireTest {

        @Test
        @DisplayName("应该成功批量设置过期时间")
        void shouldBatchExpireSuccessfully() throws Exception {
            // Given
            when(redisMonitoringService.batchExpire(anyString(), anyLong())).thenReturn(5L);

            // When & Then
            mockMvc.perform(post("/api/redis/expire")
                            .param("pattern", "hello-quick:user:*")
                            .param("expireTime", "3600"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data").value(5))
                    .andExpect(jsonPath("$.message").value("更新了 5 个缓存的过期时间"));

            verify(redisMonitoringService).batchExpire("hello-quick:user:*", 3600L);
        }

        @Test
        @DisplayName("应该使用默认模式批量设置过期时间")
        void shouldBatchExpireWithDefaultPattern() throws Exception {
            // Given
            when(redisMonitoringService.batchExpire(anyString(), anyLong())).thenReturn(0L);

            // When & Then
            mockMvc.perform(post("/api/redis/expire")
                            .param("expireTime", "1800"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisMonitoringService).batchExpire("hello-quick:*", 1800L);
        }

        @Test
        @DisplayName("应该处理无效的过期时间")
        void shouldHandleInvalidExpireTime() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/redis/expire")
                            .param("expireTime", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value("过期时间必须大于0"));

            verify(redisMonitoringService, never()).batchExpire(anyString(), anyLong());
        }

        @Test
        @DisplayName("应该处理负数过期时间")
        void shouldHandleNegativeExpireTime() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/redis/expire")
                            .param("expireTime", "-100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value("过期时间必须大于0"));

            verify(redisMonitoringService, never()).batchExpire(anyString(), anyLong());
        }

        @Test
        @DisplayName("应该处理批量设置过期时间异常")
        void shouldHandleBatchExpireException() throws Exception {
            // Given
            when(redisMonitoringService.batchExpire(anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(post("/api/redis/expire")
                            .param("expireTime", "3600"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("批量设置过期时间失败")));
        }
    }
}
