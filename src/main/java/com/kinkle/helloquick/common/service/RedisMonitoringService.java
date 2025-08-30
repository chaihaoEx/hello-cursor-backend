package com.kinkle.helloquick.common.service;

import com.kinkle.helloquick.common.util.CacheKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Redis监控服务类
 * 提供Redis缓存监控和管理功能
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMonitoringService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<String> cacheStatsScript;
    private final RedisScript<Long> cleanupExpiredCacheScript;
    private final RedisScript<Long> batchExpireScript;

    /**
     * 获取缓存统计信息
     * 
     * @param pattern 键模式
     * @return 统计信息
     */
    public Map<String, Object> getCacheStats(String pattern) {
        try {
            String result = redisTemplate.execute(cacheStatsScript, 
                Collections.emptyList(), pattern);
            
            if (result != null) {
                // 解析JSON结果（简化处理）
                Map<String, Object> stats = new HashMap<>();
                // 这里可以添加JSON解析逻辑
                stats.put("pattern", pattern);
                stats.put("rawResult", result);
                return stats;
            }
        } catch (Exception e) {
            log.error("获取缓存统计信息失败，pattern: {}", pattern, e);
        }
        
        return Map.of("error", "Failed to get cache stats");
    }

    /**
     * 获取所有缓存键
     * 
     * @param pattern 键模式
     * @return 键集合
     */
    public Set<String> getAllKeys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("获取缓存键失败，pattern: {}", pattern, e);
            return Set.of();
        }
    }

    /**
     * 获取缓存键数量
     * 
     * @param pattern 键模式
     * @return 键数量
     */
    public long getKeyCount(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.error("获取缓存键数量失败，pattern: {}", pattern, e);
            return 0;
        }
    }

    /**
     * 清理过期缓存
     * 
     * @param pattern 键模式
     * @return 清理的键数量
     */
    public long cleanupExpiredCache(String pattern) {
        try {
            Long result = redisTemplate.execute(cleanupExpiredCacheScript, 
                Collections.emptyList(), pattern);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("清理过期缓存失败，pattern: {}", pattern, e);
            return 0;
        }
    }

    /**
     * 批量设置缓存过期时间
     * 
     * @param pattern    键模式
     * @param expireTime 过期时间（秒）
     * @return 更新的键数量
     */
    public long batchExpire(String pattern, long expireTime) {
        try {
            Long result = redisTemplate.execute(batchExpireScript, 
                Collections.emptyList(), pattern, String.valueOf(expireTime));
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("批量设置过期时间失败，pattern: {}, expireTime: {}", pattern, expireTime, e);
            return 0;
        }
    }

    /**
     * 获取Redis内存使用情况
     * 
     * @return 内存使用信息
     */
    public Map<String, Object> getMemoryInfo() {
        try {
            Map<String, Object> memoryInfo = new HashMap<>();
            
            // 获取所有键
            Set<String> allKeys = redisTemplate.keys("*");
            long totalKeys = allKeys != null ? allKeys.size() : 0;
            
            // 按命名空间分组统计
            Map<String, Long> namespaceStats = new HashMap<>();
            if (allKeys != null) {
                for (String key : allKeys) {
                    String namespace = CacheKeyUtil.getNamespace(key);
                    if (namespace != null) {
                        namespaceStats.merge(namespace, 1L, Long::sum);
                    }
                }
            }
            
            memoryInfo.put("totalKeys", totalKeys);
            memoryInfo.put("namespaceStats", namespaceStats);
            memoryInfo.put("timestamp", System.currentTimeMillis());
            
            return memoryInfo;
        } catch (Exception e) {
            log.error("获取内存信息失败", e);
            return Map.of("error", "Failed to get memory info");
        }
    }

    /**
     * 获取缓存性能指标
     * 
     * @return 性能指标
     */
    public Map<String, Object> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // 测试读写性能
            String testKey = CacheKeyUtil.getTestKey("performance-test");
            String testValue = "performance-test-" + System.currentTimeMillis();
            
            long startTime = System.nanoTime();
            redisTemplate.opsForValue().set(testKey, testValue, Duration.ofSeconds(10));
            long setTime = System.nanoTime() - startTime;
            
            // 检查设置是否成功
            Object checkValue = redisTemplate.opsForValue().get(testKey);
            if (testValue.equals(checkValue)) {
                startTime = System.nanoTime();
                Object getResult = redisTemplate.opsForValue().get(testKey);
                long getTime = System.nanoTime() - startTime;
                
                // 清理测试数据
                redisTemplate.delete(testKey);
                
                metrics.put("setOperationTime", setTime / 1_000_000.0); // 转换为毫秒
                metrics.put("getOperationTime", getTime / 1_000_000.0); // 转换为毫秒
                metrics.put("setSuccess", true);
                metrics.put("getSuccess", getResult != null);
            } else {
                metrics.put("setSuccess", false);
            }
            
            metrics.put("timestamp", System.currentTimeMillis());
            
            return metrics;
        } catch (Exception e) {
            log.error("获取性能指标失败", e);
            return Map.of("error", "Failed to get performance metrics");
        }
    }

    /**
     * 获取应用缓存概览
     * 
     * @return 缓存概览信息
     */
    public Map<String, Object> getCacheOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 获取各命名空间的键数量
            Map<String, Long> namespaceCounts = new HashMap<>();
            String[] namespaces = {"user", "session", "rate-limit", "verification", "test", "role", "permission", "config"};
            
            for (String namespace : namespaces) {
                String pattern = CacheKeyUtil.buildKey(namespace, "*");
                long count = getKeyCount(pattern);
                if (count > 0) {
                    namespaceCounts.put(namespace, count);
                }
            }
            
            overview.put("namespaceCounts", namespaceCounts);
            overview.put("totalKeys", namespaceCounts.values().stream().mapToLong(Long::longValue).sum());
            overview.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("获取缓存概览失败", e);
            overview.put("error", "Failed to get cache overview");
        }
        
        return overview;
    }
}
