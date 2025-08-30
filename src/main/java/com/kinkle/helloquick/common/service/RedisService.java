package com.kinkle.helloquick.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis服务类 - 基于Spring Boot最佳实践重构
 * 封装常用的Redis操作，提供类型安全和更好的错误处理
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // 预定义操作对象，避免重复获取
    private ValueOperations<String, Object> valueOps;
    private HashOperations<String, String, Object> hashOps;
    private SetOperations<String, Object> setOps;
    private ListOperations<String, Object> listOps;
    
    /**
     * 获取ValueOperations实例
     */
    private ValueOperations<String, Object> getValueOps() {
        if (valueOps == null) {
            valueOps = redisTemplate.opsForValue();
        }
        return valueOps;
    }
    
    /**
     * 获取HashOperations实例
     */
    private HashOperations<String, String, Object> getHashOps() {
        if (hashOps == null) {
            hashOps = redisTemplate.opsForHash();
        }
        return hashOps;
    }
    
    /**
     * 获取SetOperations实例
     */
    private SetOperations<String, Object> getSetOps() {
        if (setOps == null) {
            setOps = redisTemplate.opsForSet();
        }
        return setOps;
    }
    
    /**
     * 获取ListOperations实例
     */
    private ListOperations<String, Object> getListOps() {
        if (listOps == null) {
            listOps = redisTemplate.opsForList();
        }
        return listOps;
    }
    
    /**
     * 执行Redis操作的安全包装器
     */
    private <T> T executeSafely(Supplier<T> operation, String operationName, Object... params) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.error("Redis操作失败: {}, 参数: {}", operationName, params, e);
            return null;
        }
    }
    
    /**
     * 执行Redis操作的安全包装器（返回boolean）
     */
    private boolean executeSafelyBoolean(Supplier<Boolean> operation, String operationName, Object... params) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.error("Redis操作失败: {}, 参数: {}", operationName, params, e);
            return false;
        }
    }

    // =============================common============================

    /**
     * 指定缓存失效时间
     * 
     * @param key  键
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean expire(String key, long time) {
        if (key == null || time <= 0) {
            return false;
        }
        return executeSafelyBoolean(
            () -> redisTemplate.expire(key, time, TimeUnit.SECONDS),
            "设置缓存失效时间", key, time
        );
    }

    /**
     * 指定缓存失效时间（使用Duration）
     * 
     * @param key      键
     * @param duration 时间间隔
     * @return true成功 false失败
     */
    public boolean expire(String key, Duration duration) {
        if (key == null || duration == null || duration.isNegative() || duration.isZero()) {
            return false;
        }
        return executeSafelyBoolean(
            () -> redisTemplate.expire(key, duration),
            "设置缓存失效时间(Duration)", key, duration
        );
    }

    /**
     * 根据key获取过期时间
     * 
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效，-1表示key不存在，-2表示key存在但没有过期时间
     */
    public long getExpire(String key) {
        if (key == null) {
            return -1;
        }
        Long expire = executeSafely(
            () -> redisTemplate.getExpire(key, TimeUnit.SECONDS),
            "获取缓存过期时间", key
        );
        return expire != null ? expire : -1;
    }

    /**
     * 判断key是否存在
     * 
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        if (key == null) {
            return false;
        }
        Boolean result = executeSafely(
            () -> redisTemplate.hasKey(key),
            "判断key是否存在", key
        );
        return Boolean.TRUE.equals(result);
    }

    /**
     * 删除缓存
     * 
     * @param key 可以传一个值 或多个
     * @return 删除的key数量
     */
    public long del(String... key) {
        if (key == null || key.length == 0) {
            return 0;
        }
        Long result = executeSafely(
            () -> redisTemplate.delete(List.of(key)),
            "删除缓存", (Object) key
        );
        return result != null ? result : 0;
    }

    /**
     * 删除缓存（单个key）
     * 
     * @param key 键
     * @return true成功 false失败
     */
    public boolean del(String key) {
        if (key == null) {
            return false;
        }
        Boolean result = executeSafely(
            () -> redisTemplate.delete(key),
            "删除单个缓存", key
        );
        return Boolean.TRUE.equals(result);
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        if (key == null) {
            return null;
        }
        return executeSafely(
            () -> getValueOps().get(key),
            "获取缓存", key
        );
    }

    /**
     * 普通缓存获取（带类型转换）
     * 
     * @param key   键
     * @param clazz 目标类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            log.warn("类型转换失败，key: {}, expected: {}, actual: {}", key, clazz, value.getClass());
            return null;
        }
    }

    /**
     * 普通缓存放入
     * 
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        if (key == null) {
            return false;
        }
        return executeSafelyBoolean(
            () -> {
                getValueOps().set(key, value);
                return true;
            },
            "设置缓存", key, value
        );
    }

    /**
     * 普通缓存放入并设置时间
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        if (key == null) {
            return false;
        }
        if (time > 0) {
            return executeSafelyBoolean(
                () -> {
                    getValueOps().set(key, value, time, TimeUnit.SECONDS);
                    return true;
                },
                "设置缓存(带过期时间)", key, value, time
            );
        } else {
            return set(key, value);
        }
    }

    /**
     * 普通缓存放入并设置时间（使用Duration）
     * 
     * @param key      键
     * @param value    值
     * @param duration 时间间隔
     * @return true成功 false失败
     */
    public boolean set(String key, Object value, Duration duration) {
        if (key == null || duration == null || duration.isNegative() || duration.isZero()) {
            return false;
        }
        return executeSafelyBoolean(
            () -> {
                getValueOps().set(key, value, duration);
                return true;
            },
            "设置缓存(带Duration)", key, value, duration
        );
    }

    /**
     * 递增
     * 
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (key == null || delta < 0) {
            throw new IllegalArgumentException("key不能为null，delta必须大于等于0");
        }
        Long result = executeSafely(
            () -> getValueOps().increment(key, delta),
            "递增操作", key, delta
        );
        return result != null ? result : 0;
    }

    /**
     * 递减
     * 
     * @param key   键
     * @param delta 要减少几(大于0)
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (key == null || delta < 0) {
            throw new IllegalArgumentException("key不能为null，delta必须大于等于0");
        }
        Long result = executeSafely(
            () -> getValueOps().increment(key, -delta),
            "递减操作", key, delta
        );
        return result != null ? result : 0;
    }

    /**
     * 原子性递增（从1开始）
     * 
     * @param key 键
     * @return 递增后的值
     */
    public long incr(String key) {
        return incr(key, 1);
    }

    /**
     * 原子性递减（减1）
     * 
     * @param key 键
     * @return 递减后的值
     */
    public long decr(String key) {
        return decr(key, 1);
    }

    // ================================Map=================================

    /**
     * HashGet
     * 
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return getHashOps().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * 
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String, Object> hmget(String key) {
        return getHashOps().entries(key);
    }

    /**
     * HashSet
     * 
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            getHashOps().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("HashSet失败，key: {}, map: {}", key, map, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * 
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            getHashOps().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("HashSet失败，key: {}, map: {}, time: {}", key, map, time, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            getHashOps().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("向hash表放入数据失败，key: {}, item: {}, value: {}", key, item, value, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            getHashOps().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("向hash表放入数据失败，key: {}, item: {}, value: {}, time: {}", key, item, value, time, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * 
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     * 
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     * 
     * @param key 键
     * @return Set集合
     */
    public Set<Object> sGet(String key) {
        try {
            return getSetOps().members(key);
        } catch (Exception e) {
            log.error("获取Set失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * 
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(getSetOps().isMember(key, value));
        } catch (Exception e) {
            log.error("查询Set中是否存在值失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * 
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            Long count = getSetOps().add(key, values);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("将数据放入set缓存失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * 
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = getSetOps().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("将set数据放入缓存失败，key: {}, time: {}, values: {}", key, time, values, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long sGetSetSize(String key) {
        try {
            Long size = getSetOps().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("获取set缓存长度失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     * 
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = getSetOps().remove(key, values);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("移除set中的值失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     * 
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return list内容
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return getListOps().range(key, start, end);
        } catch (Exception e) {
            log.error("获取list缓存内容失败，key: {}, start: {}, end: {}", key, start, end, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long lGetListSize(String key) {
        try {
            Long size = getListOps().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("获取list缓存长度失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * 
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return getListOps().index(key, index);
        } catch (Exception e) {
            log.error("通过索引获取list中的值失败，key: {}, index: {}", key, index, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean lSet(String key, Object value) {
        try {
            getListOps().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("将list放入缓存失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return true成功 false失败
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            getListOps().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("将list放入缓存失败，key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            getListOps().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("将list放入缓存失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return true成功 false失败
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            getListOps().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("将list放入缓存失败，key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * 
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return true成功 false失败
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            getListOps().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("根据索引修改list中的数据失败，key: {}, index: {}, value: {}", key, index, value, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * 
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = getListOps().remove(key, count, value);
            return remove != null ? remove : 0L;
        } catch (Exception e) {
            log.error("移除list中的值失败，key: {}, count: {}, value: {}", key, count, value, e);
            return 0;
        }
    }
}
