package com.kinkle.helloquick.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;


import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisService 单元测试
 * 
 * @author kinkle
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisService 单元测试")
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        // 使用 lenient 模式避免不必要的 stubbing 错误
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        
        // 重置 RedisService 的内部状态
        redisService = new RedisService(redisTemplate);
    }

    @Nested
    @DisplayName("String 操作测试")
    class StringOperationsTest {

        @Test
        @DisplayName("应该成功获取缓存")
        void shouldGetCacheSuccessfully() {
            // Given
            String key = "test:key";
            String value = "test:value";
            when(valueOperations.get(key)).thenReturn(value);

            // When
            Object result = redisService.get(key);

            // Then
            assertThat(result).isEqualTo(value);
            verify(valueOperations).get(key);
        }

        @Test
        @DisplayName("应该成功获取指定类型的缓存")
        void shouldGetCacheWithTypeSuccessfully() {
            // Given
            String key = "test:key";
            String value = "test:value";
            when(valueOperations.get(key)).thenReturn(value);

            // When
            String result = redisService.get(key, String.class);

            // Then
            assertThat(result).isEqualTo(value);
        }

        @Test
        @DisplayName("应该成功设置缓存")
        void shouldSetCacheSuccessfully() {
            // Given
            String key = "test:key";
            String value = "test:value";
            doNothing().when(valueOperations).set(key, value);

            // When
            boolean result = redisService.set(key, value);

            // Then
            assertThat(result).isTrue();
            verify(valueOperations).set(key, value);
        }

        @Test
        @DisplayName("应该成功设置带过期时间的缓存")
        void shouldSetCacheWithExpireSuccessfully() {
            // Given
            String key = "test:key";
            String value = "test:value";
            long time = 3600L;
            doNothing().when(valueOperations).set(eq(key), eq(value), eq(time), eq(TimeUnit.SECONDS));

            // When
            boolean result = redisService.set(key, value, time);

            // Then
            assertThat(result).isTrue();
            verify(valueOperations).set(eq(key), eq(value), eq(time), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("应该成功删除缓存")
        void shouldDeleteCacheSuccessfully() {
            // Given
            String key = "test:key";
            when(redisTemplate.delete(key)).thenReturn(true);

            // When
            boolean result = redisService.del(key);

            // Then
            assertThat(result).isTrue();
            verify(redisTemplate).delete(key);
        }

        @Test
        @DisplayName("应该成功检查键是否存在")
        void shouldCheckKeyExistsSuccessfully() {
            // Given
            String key = "test:key";
            when(redisTemplate.hasKey(key)).thenReturn(true);

            // When
            boolean result = redisService.hasKey(key);

            // Then
            assertThat(result).isTrue();
            verify(redisTemplate).hasKey(key);
        }

        @Test
        @DisplayName("应该成功设置过期时间")
        void shouldSetExpireSuccessfully() {
            // Given
            String key = "test:key";
            long time = 3600L;
            when(redisTemplate.expire(key, time, TimeUnit.SECONDS)).thenReturn(true);

            // When
            boolean result = redisService.expire(key, time);

            // Then
            assertThat(result).isTrue();
            verify(redisTemplate).expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Nested
    @DisplayName("Hash 操作测试")
    class HashOperationsTest {

        @Test
        @DisplayName("应该成功获取Hash值")
        void shouldGetHashValueSuccessfully() {
            // Given
            String key = "test:hash";
            String item = "field1";
            String value = "value1";
            when(hashOperations.get(key, item)).thenReturn(value);

            // When
            Object result = redisService.hget(key, item);

            // Then
            assertThat(result).isEqualTo(value);
            verify(hashOperations).get(key, item);
        }

        @Test
        @DisplayName("应该成功设置Hash值")
        void shouldSetHashValueSuccessfully() {
            // Given
            String key = "test:hash";
            String item = "field1";
            String value = "value1";
            doNothing().when(hashOperations).put(key, item, value);

            // When
            boolean result = redisService.hset(key, item, value);

            // Then
            assertThat(result).isTrue();
            verify(hashOperations).put(key, item, value);
        }

        @Test
        @DisplayName("应该成功批量设置Hash值")
        void shouldSetHashValuesSuccessfully() {
            // Given
            String key = "test:hash";
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("field1", "value1");
            hashMap.put("field2", "value2");
            doNothing().when(hashOperations).putAll(key, hashMap);

            // When
            boolean result = redisService.hmset(key, hashMap);

            // Then
            assertThat(result).isTrue();
            verify(hashOperations).putAll(key, hashMap);
        }

        @Test
        @DisplayName("应该成功获取所有Hash值")
        void shouldGetAllHashValuesSuccessfully() {
            // Given
            String key = "test:hash";
            Map<Object, Object> hashMap = new HashMap<>();
            hashMap.put("field1", "value1");
            hashMap.put("field2", "value2");
            when(hashOperations.entries(key)).thenReturn(hashMap);

            // When
            Map<String, Object> result = redisService.hmget(key);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsEntry("field1", "value1");
            assertThat(result).containsEntry("field2", "value2");
            verify(hashOperations).entries(key);
        }

        @Test
        @DisplayName("应该成功删除Hash字段")
        void shouldDeleteHashFieldSuccessfully() {
            // Given
            String key = "test:hash";
            Object[] items = {"field1", "field2"};
            when(hashOperations.delete(key, items)).thenReturn(2L);

            // When
            redisService.hdel(key, items);

            // Then
            verify(hashOperations).delete(key, items);
        }

        @Test
        @DisplayName("应该成功检查Hash字段是否存在")
        void shouldCheckHashFieldExistsSuccessfully() {
            // Given
            String key = "test:hash";
            String item = "field1";
            when(hashOperations.hasKey(key, item)).thenReturn(true);

            // When
            boolean result = redisService.hHasKey(key, item);

            // Then
            assertThat(result).isTrue();
            verify(hashOperations).hasKey(key, item);
        }
    }

    @Nested
    @DisplayName("Set 操作测试")
    class SetOperationsTest {

        @Test
        @DisplayName("应该成功获取Set值")
        void shouldGetSetValuesSuccessfully() {
            // Given
            String key = "test:set";
            Set<Object> values = Set.of("value1", "value2");
            when(setOperations.members(key)).thenReturn(values);

            // When
            Set<Object> result = redisService.sGet(key);

            // Then
            assertThat(result).isEqualTo(values);
            verify(setOperations).members(key);
        }

        @Test
        @DisplayName("应该成功向Set添加值")
        void shouldAddToSetSuccessfully() {
            // Given
            String key = "test:set";
            Object[] values = {"value1", "value2"};
            when(setOperations.add(key, values)).thenReturn(2L);

            // When
            long result = redisService.sSet(key, values);

            // Then
            assertThat(result).isEqualTo(2L);
            verify(setOperations).add(key, values);
        }

        @Test
        @DisplayName("应该成功向Set添加值并设置过期时间")
        void shouldAddToSetWithExpireSuccessfully() {
            // Given
            String key = "test:set";
            Object[] values = {"value1", "value2"};
            long time = 3600L;
            when(setOperations.add(key, values)).thenReturn(2L);

            // When
            long result = redisService.sSetAndTime(key, time, values);

            // Then
            assertThat(result).isEqualTo(2L);
            verify(setOperations).add(key, values);
            verify(redisTemplate).expire(key, time, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("应该成功检查Set值是否存在")
        void shouldCheckSetValueExistsSuccessfully() {
            // Given
            String key = "test:set";
            Object value = "value1";
            when(setOperations.isMember(key, value)).thenReturn(true);

            // When
            boolean result = redisService.sHasKey(key, value);

            // Then
            assertThat(result).isTrue();
            verify(setOperations).isMember(key, value);
        }

        @Test
        @DisplayName("应该成功获取Set大小")
        void shouldGetSetSizeSuccessfully() {
            // Given
            String key = "test:set";
            when(setOperations.size(key)).thenReturn(3L);

            // When
            long result = redisService.sGetSetSize(key);

            // Then
            assertThat(result).isEqualTo(3L);
            verify(setOperations).size(key);
        }

        @Test
        @DisplayName("应该成功从Set删除值")
        void shouldRemoveFromSetSuccessfully() {
            // Given
            String key = "test:set";
            Object[] values = {"value1", "value2"};
            when(setOperations.remove(key, values)).thenReturn(2L);

            // When
            long result = redisService.setRemove(key, values);

            // Then
            assertThat(result).isEqualTo(2L);
            verify(setOperations).remove(key, values);
        }
    }

    @Nested
    @DisplayName("List 操作测试")
    class ListOperationsTest {

        @Test
        @DisplayName("应该成功获取List内容")
        void shouldGetListContentSuccessfully() {
            // Given
            String key = "test:list";
            long start = 0L;
            long end = -1L;
            List<Object> values = List.of("value1", "value2");
            when(listOperations.range(key, start, end)).thenReturn(values);

            // When
            List<Object> result = redisService.lGet(key, start, end);

            // Then
            assertThat(result).isEqualTo(values);
            verify(listOperations).range(key, start, end);
        }

        @Test
        @DisplayName("应该成功获取List大小")
        void shouldGetListSizeSuccessfully() {
            // Given
            String key = "test:list";
            when(listOperations.size(key)).thenReturn(3L);

            // When
            long result = redisService.lGetListSize(key);

            // Then
            assertThat(result).isEqualTo(3L);
            verify(listOperations).size(key);
        }

        @Test
        @DisplayName("应该成功向List添加值")
        void shouldAddToListSuccessfully() {
            // Given
            String key = "test:list";
            Object value = "newItem";
            when(listOperations.rightPush(key, value)).thenReturn(1L);

            // When
            boolean result = redisService.lSet(key, value);

            // Then
            assertThat(result).isTrue();
            verify(listOperations).rightPush(key, value);
        }

        @Test
        @DisplayName("应该成功根据索引获取List值")
        void shouldGetListValueByIndexSuccessfully() {
            // Given
            String key = "test:list";
            long index = 1L;
            String value = "value1";
            when(listOperations.index(key, index)).thenReturn(value);

            // When
            Object result = redisService.lGetIndex(key, index);

            // Then
            assertThat(result).isEqualTo(value);
            verify(listOperations).index(key, index);
        }

        @Test
        @DisplayName("应该成功根据索引修改List值")
        void shouldUpdateListValueByIndexSuccessfully() {
            // Given
            String key = "test:list";
            long index = 1L;
            Object value = "updatedItem";
            doNothing().when(listOperations).set(key, index, value);

            // When
            boolean result = redisService.lUpdateIndex(key, index, value);

            // Then
            assertThat(result).isTrue();
            verify(listOperations).set(key, index, value);
        }

        @Test
        @DisplayName("应该成功从List删除值")
        void shouldRemoveFromListSuccessfully() {
            // Given
            String key = "test:list";
            long count = 1L;
            Object value = "value1";
            when(listOperations.remove(key, count, value)).thenReturn(1L);

            // When
            long result = redisService.lRemove(key, count, value);

            // Then
            assertThat(result).isEqualTo(1L);
            verify(listOperations).remove(key, count, value);
        }
    }

    @Nested
    @DisplayName("通用操作测试")
    class CommonOperationsTest {

        @Test
        @DisplayName("应该成功批量删除键")
        void shouldDeleteKeysSuccessfully() {
            // Given
            String[] keys = {"key1", "key2", "key3"};
            when(redisTemplate.delete(Arrays.asList(keys))).thenReturn(3L);

            // When
            long result = redisService.del(keys);

            // Then
            assertThat(result).isEqualTo(3L);
            verify(redisTemplate).delete(Arrays.asList(keys));
        }

        @Test
        @DisplayName("应该处理空删除数组")
        void shouldHandleEmptyDeleteArray() {
            // Given
            String[] keys = {};

            // When
            long result = redisService.del(keys);

            // Then
            assertThat(result).isEqualTo(0L);
            verify(redisTemplate, never()).delete(any(String.class));
        }

        @Test
        @DisplayName("应该处理null删除数组")
        void shouldHandleNullDeleteArray() {
            // Given
            String[] keys = null;

            // When
            long result = redisService.del(keys);

            // Then
            assertThat(result).isEqualTo(0L);
            verify(redisTemplate, never()).delete(any(String.class));
        }
    }
}