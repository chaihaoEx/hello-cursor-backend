package com.kinkle.helloquick.controller;

import com.kinkle.helloquick.common.result.Result;
import com.kinkle.helloquick.common.result.ResultCode;
import com.kinkle.helloquick.common.service.RedisService;
import com.kinkle.helloquick.common.util.CacheKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存测试控制器
 * 用于测试Redis缓存功能
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheTestController {

    private final RedisService redisService;

    /**
     * 测试Redis连接
     */
    @GetMapping("/ping")
    public Result<String> ping() {
        try {
            String testKey = CacheKeyUtil.getPingTestKey();
            String testValue = "Hello Redis! " + LocalDateTime.now();
            
            // 设置缓存
            redisService.set(testKey, testValue, 60);
            
            // 获取缓存
            Object cachedValue = redisService.get(testKey);
            
            log.info("Redis ping测试成功: {}", cachedValue);
            return Result.success("Redis连接正常: " + cachedValue);
        } catch (Exception e) {
            log.error("Redis ping测试失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "Redis连接失败: " + e.getMessage());
        }
    }

    /**
     * 设置缓存
     */
    @PostMapping("/set")
    public Result<String> setCache(@RequestParam String key, 
                                   @RequestParam String value,
                                   @RequestParam(defaultValue = "300") long ttl) {
        try {
            String cacheKey = CacheKeyUtil.getTestKey(key);
            boolean success = redisService.set(cacheKey, value, ttl);
            
            if (success) {
                log.info("设置缓存成功: key={}, value={}, ttl={}", cacheKey, value, ttl);
                return Result.success("缓存设置成功");
            } else {
                return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "缓存设置失败");
            }
        } catch (Exception e) {
            log.error("设置缓存失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "缓存设置失败: " + e.getMessage());
        }
    }

    /**
     * 获取缓存
     */
    @GetMapping("/get/{key}")
    public Result<Object> getCache(@PathVariable String key) {
        try {
            String cacheKey = CacheKeyUtil.getTestKey(key);
            Object value = redisService.get(cacheKey);
            
            if (value != null) {
                log.info("获取缓存成功: key={}, value={}", cacheKey, value);
                return Result.success(value);
            } else {
                return Result.failure(ResultCode.DATA_NOT_FOUND, "缓存不存在或已过期");
            }
        } catch (Exception e) {
            log.error("获取缓存失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取缓存失败: " + e.getMessage());
        }
    }

    /**
     * 删除缓存
     */
    @DeleteMapping("/delete/{key}")
    public Result<String> deleteCache(@PathVariable String key) {
        try {
            String cacheKey = CacheKeyUtil.getTestKey(key);
            redisService.del(cacheKey);
            
            log.info("删除缓存成功: key={}", cacheKey);
            return Result.success("缓存删除成功");
        } catch (Exception e) {
            log.error("删除缓存失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "删除缓存失败: " + e.getMessage());
        }
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCacheStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 测试不同类型的缓存操作
            String stringKey = CacheKeyUtil.getStatsTestKey("string");
            String counterKey = CacheKeyUtil.getStatsTestKey("counter");
            String hashKey = CacheKeyUtil.getStatsTestKey("hash");
            
            // 字符串操作
            redisService.set(stringKey, "test-value", 60);
            stats.put("string_test", redisService.get(stringKey));
            
            // 计数器操作
            redisService.set(counterKey, "0", 60);
            long counter = redisService.incr(counterKey, 1);
            stats.put("counter_test", counter);
            
            // Hash操作
            Map<String, Object> hashData = new HashMap<>();
            hashData.put("field1", "value1");
            hashData.put("field2", "value2");
            redisService.hmset(hashKey, hashData, 60);
            stats.put("hash_test", redisService.hmget(hashKey));
            
            // 检查key是否存在
            stats.put("key_exists", redisService.hasKey(stringKey));
            
            // 获取过期时间
            stats.put("ttl", redisService.getExpire(stringKey));
            
            log.info("获取缓存统计信息成功: {}", stats);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取缓存统计信息失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取缓存统计信息失败: " + e.getMessage());
        }
    }
}
