package com.kinkle.helloquick.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Redis监控配置类
 * 配置Redis相关的监控和指标收集
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Configuration
public class RedisMonitoringConfig {

    /**
     * 自定义MeterRegistry配置
     * 为Redis和缓存指标添加标签
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
                "application", "hello-quick",
                "component", "redis"
        );
    }

    /**
     * Redis缓存指标收集
     * 为Redis缓存管理器添加指标收集
     * 注意：CacheMetrics需要额外的micrometer-cache依赖
     */
    // @Bean
    // public CacheMetrics redisCacheMetrics(CacheManager cacheManager) {
    //     if (cacheManager instanceof RedisCacheManager) {
    //         log.info("配置Redis缓存指标收集");
    //         return new CacheMetrics(cacheManager, "redis.cache");
    //     }
    //     return null;
    // }

    /**
     * Redis Lua脚本 - 获取缓存统计信息
     */
    @Bean
    public RedisScript<String> cacheStatsScript() {
        String script = """
            local keys = redis.call('keys', ARGV[1])
            local total = #keys
            local expired = 0
            local ttl_sum = 0
            
            for i = 1, total do
                local ttl = redis.call('ttl', keys[i])
                if ttl == -2 then
                    expired = expired + 1
                elseif ttl > 0 then
                    ttl_sum = ttl_sum + ttl
                end
            end
            
            local avg_ttl = total > 0 and (ttl_sum / (total - expired)) or 0
            
            return string.format('{"total":%d,"expired":%d,"active":%d,"avg_ttl":%.2f}', 
                                total, expired, total - expired, avg_ttl)
            """;
        
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(String.class);
        return redisScript;
    }

    /**
     * Redis Lua脚本 - 清理过期缓存
     */
    @Bean
    public RedisScript<Long> cleanupExpiredCacheScript() {
        String script = """
            local pattern = ARGV[1]
            local keys = redis.call('keys', pattern)
            local deleted = 0
            
            for i = 1, #keys do
                local ttl = redis.call('ttl', keys[i])
                if ttl == -2 then
                    redis.call('del', keys[i])
                    deleted = deleted + 1
                end
            end
            
            return deleted
            """;
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * Redis Lua脚本 - 批量设置缓存过期时间
     */
    @Bean
    public RedisScript<Long> batchExpireScript() {
        String script = """
            local pattern = ARGV[1]
            local expire_time = tonumber(ARGV[2])
            local keys = redis.call('keys', pattern)
            local updated = 0
            
            for i = 1, #keys do
                if redis.call('exists', keys[i]) == 1 then
                    redis.call('expire', keys[i], expire_time)
                    updated = updated + 1
                end
            end
            
            return updated
            """;
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
