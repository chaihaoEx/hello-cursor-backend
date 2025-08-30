package com.kinkle.helloquick.controller;

import com.kinkle.helloquick.common.result.ResultCode;
import com.kinkle.helloquick.common.service.RedisService;
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
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CacheTestController 单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("缓存测试控制器测试")
class CacheTestControllerTest {

    @Mock
    private RedisService redisService;

    private CacheTestController cacheTestController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        cacheTestController = new CacheTestController(redisService);
        mockMvc = MockMvcBuilders.standaloneSetup(cacheTestController).build();
    }

    @Nested
    @DisplayName("Redis连接测试")
    class PingTest {

        @Test
        @DisplayName("应该成功测试Redis连接")
        void shouldPingRedisSuccessfully() throws Exception {
            // Given
            String testValue = "Hello Redis! 2023-01-01T10:00:00";
            when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);
            when(redisService.get(anyString())).thenReturn(testValue);

            // When & Then
            mockMvc.perform(get("/api/cache/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value(containsString("Redis连接正常")));

            verify(redisService).set(anyString(), anyString(), eq(60L));
            verify(redisService).get(anyString());
        }

        @Test
        @DisplayName("应该处理Redis连接失败")
        void shouldHandleRedisConnectionFailure() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Redis连接失败"));

            // When & Then
            mockMvc.perform(get("/api/cache/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("Redis连接失败")));
        }
    }

    @Nested
    @DisplayName("缓存设置测试")
    class SetCacheTest {

        @Test
        @DisplayName("应该成功设置缓存")
        void shouldSetCacheSuccessfully() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);

            // When & Then
            mockMvc.perform(post("/api/cache/set")
                            .param("key", "test-key")
                            .param("value", "test-value")
                            .param("ttl", "300"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value("缓存设置成功"));

            verify(redisService).set(anyString(), eq("test-value"), eq(300L));
        }

        @Test
        @DisplayName("应该使用默认TTL设置缓存")
        void shouldSetCacheWithDefaultTtl() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);

            // When & Then
            mockMvc.perform(post("/api/cache/set")
                            .param("key", "test-key")
                            .param("value", "test-value"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

            verify(redisService).set(anyString(), eq("test-value"), eq(300L));
        }

        @Test
        @DisplayName("应该处理缓存设置失败")
        void shouldHandleSetCacheFailure() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(false);

            // When & Then
            mockMvc.perform(post("/api/cache/set")
                            .param("key", "test-key")
                            .param("value", "test-value"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value("缓存设置失败"));
        }

        @Test
        @DisplayName("应该处理设置缓存异常")
        void shouldHandleSetCacheException() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(post("/api/cache/set")
                            .param("key", "test-key")
                            .param("value", "test-value"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("缓存设置失败")));
        }
    }

    @Nested
    @DisplayName("缓存获取测试")
    class GetCacheTest {

        @Test
        @DisplayName("应该成功获取缓存")
        void shouldGetCacheSuccessfully() throws Exception {
            // Given
            String testValue = "test-value";
            when(redisService.get(anyString())).thenReturn(testValue);

            // When & Then
            mockMvc.perform(get("/api/cache/get/test-key"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value(testValue));

            verify(redisService).get(anyString());
        }

        @Test
        @DisplayName("应该处理缓存不存在")
        void shouldHandleCacheNotFound() throws Exception {
            // Given
            when(redisService.get(anyString())).thenReturn(null);

            // When & Then
            mockMvc.perform(get("/api/cache/get/nonexistent-key"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.DATA_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value("缓存不存在或已过期"));
        }

        @Test
        @DisplayName("应该处理获取缓存异常")
        void shouldHandleGetCacheException() throws Exception {
            // Given
            when(redisService.get(anyString()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/cache/get/test-key"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取缓存失败")));
        }
    }

    @Nested
    @DisplayName("缓存删除测试")
    class DeleteCacheTest {

        @Test
        @DisplayName("应该成功删除缓存")
        void shouldDeleteCacheSuccessfully() throws Exception {
            // Given
            when(redisService.del(anyString())).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/cache/delete/test-key"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value("缓存删除成功"));

            verify(redisService).del(anyString());
        }

        @Test
        @DisplayName("应该处理删除缓存异常")
        void shouldHandleDeleteCacheException() throws Exception {
            // Given
            when(redisService.del(anyString())).thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(delete("/api/cache/delete/test-key"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("删除缓存失败")));
        }
    }

    @Nested
    @DisplayName("缓存统计测试")
    class CacheStatsTest {

        @Test
        @DisplayName("应该成功获取缓存统计信息")
        void shouldGetCacheStatsSuccessfully() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);
            when(redisService.get(anyString())).thenReturn("test-value");
            when(redisService.incr(anyString(), anyLong())).thenReturn(1L);
            when(redisService.hmset(anyString(), anyMap(), anyLong())).thenReturn(true);
            when(redisService.hmget(anyString())).thenReturn(new HashMap<>());
            when(redisService.hasKey(anyString())).thenReturn(true);
            when(redisService.getExpire(anyString())).thenReturn(60L);

            // When & Then
            mockMvc.perform(get("/api/cache/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data.string_test").value("test-value"))
                    .andExpect(jsonPath("$.data.counter_test").value(1))
                    .andExpect(jsonPath("$.data.key_exists").value(true))
                    .andExpect(jsonPath("$.data.ttl").value(60));

            verify(redisService, atLeastOnce()).set(anyString(), anyString(), anyLong());
            verify(redisService, atLeastOnce()).get(anyString());
            verify(redisService).incr(anyString(), anyLong());
            verify(redisService).hmset(anyString(), anyMap(), anyLong());
            verify(redisService).hmget(anyString());
            verify(redisService).hasKey(anyString());
            verify(redisService).getExpire(anyString());
        }

        @Test
        @DisplayName("应该处理获取缓存统计信息异常")
        void shouldHandleGetCacheStatsException() throws Exception {
            // Given
            when(redisService.set(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Redis异常"));

            // When & Then
            mockMvc.perform(get("/api/cache/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.INTERNAL_SERVER_ERROR.getCode()))
                    .andExpect(jsonPath("$.message").value(containsString("获取缓存统计信息失败")));
        }
    }
}
