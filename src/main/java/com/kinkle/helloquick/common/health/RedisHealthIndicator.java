package com.kinkle.helloquick.common.health;

import com.kinkle.helloquick.common.service.RedisService;
import com.kinkle.helloquick.common.util.CacheKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis健康检查指示器 - 基于Spring Boot最佳实践重构
 * 使用RedisService进行健康检查，避免直接使用RedisTemplate
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisService redisService;

    @Override
    public Health health() {
        try {
            // 使用RedisService进行健康检查
            String testKey = CacheKeyUtil.getPingTestKey();
            String testValue = "health-check-" + System.currentTimeMillis();
            
            // 测试设置和获取操作
            boolean setResult = redisService.set(testKey, testValue, Duration.ofSeconds(10));
            if (!setResult) {
                return Health.down()
                        .withDetail("redis", "Set operation failed")
                        .withDetail("testKey", testKey)
                        .build();
            }
            
            // 获取设置的值
            String retrievedValue = redisService.get(testKey, String.class);
            if (testValue.equals(retrievedValue)) {
                // 清理测试数据
                redisService.del(testKey);
                
                // 获取Redis信息
                long expireTime = redisService.getExpire(testKey);
                boolean hasKey = redisService.hasKey(testKey);
                
                return Health.up()
                        .withDetail("redis", "Available")
                        .withDetail("test", "Set/Get successful")
                        .withDetail("testKey", testKey)
                        .withDetail("expireTime", expireTime)
                        .withDetail("hasKey", hasKey)
                        .build();
            } else {
                return Health.down()
                        .withDetail("redis", "Get operation failed")
                        .withDetail("expected", testValue)
                        .withDetail("actual", retrievedValue)
                        .withDetail("testKey", testKey)
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis健康检查失败", e);
            return Health.down()
                    .withDetail("redis", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .build();
        }
    }
}
