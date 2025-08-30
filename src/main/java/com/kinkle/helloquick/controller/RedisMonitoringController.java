package com.kinkle.helloquick.controller;

import com.kinkle.helloquick.common.result.Result;
import com.kinkle.helloquick.common.result.ResultCode;
import com.kinkle.helloquick.common.service.RedisMonitoringService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Redis监控控制器
 * 提供Redis缓存监控和管理API
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
// @Tag(name = "Redis监控", description = "Redis缓存监控和管理API")
public class RedisMonitoringController {

    private final RedisMonitoringService redisMonitoringService;

    /**
     * 获取缓存概览
     */
    @GetMapping("/overview")
    // @Operation(summary = "获取缓存概览", description = "获取Redis缓存的整体概览信息")
    public Result<Map<String, Object>> getCacheOverview() {
        try {
            Map<String, Object> overview = redisMonitoringService.getCacheOverview();
            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取缓存概览失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取缓存概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    // @Operation(summary = "获取缓存统计", description = "获取指定模式的缓存统计信息")
    public Result<Map<String, Object>> getCacheStats(
            // @Parameter(description = "键模式，如 hello-quick:user:*") 
            @RequestParam(defaultValue = "hello-quick:*") String pattern) {
        try {
            Map<String, Object> stats = redisMonitoringService.getCacheStats(pattern);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取缓存统计失败，pattern: {}", pattern, e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取缓存统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有缓存键
     */
    @GetMapping("/keys")
    // @Operation(summary = "获取缓存键", description = "获取指定模式的所有缓存键")
    public Result<Set<String>> getAllKeys(
            // @Parameter(description = "键模式，如 hello-quick:user:*") 
            @RequestParam(defaultValue = "hello-quick:*") String pattern) {
        try {
            Set<String> keys = redisMonitoringService.getAllKeys(pattern);
            return Result.success(keys);
        } catch (Exception e) {
            log.error("获取缓存键失败，pattern: {}", pattern, e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取缓存键失败: " + e.getMessage());
        }
    }

    /**
     * 获取缓存键数量
     */
    @GetMapping("/count")
    // @Operation(summary = "获取键数量", description = "获取指定模式的缓存键数量")
    public Result<Long> getKeyCount(
            // @Parameter(description = "键模式，如 hello-quick:user:*") 
            @RequestParam(defaultValue = "hello-quick:*") String pattern) {
        try {
            long count = redisMonitoringService.getKeyCount(pattern);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取键数量失败，pattern: {}", pattern, e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取键数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取内存使用情况
     */
    @GetMapping("/memory")
    // @Operation(summary = "获取内存信息", description = "获取Redis内存使用情况")
    public Result<Map<String, Object>> getMemoryInfo() {
        try {
            Map<String, Object> memoryInfo = redisMonitoringService.getMemoryInfo();
            return Result.success(memoryInfo);
        } catch (Exception e) {
            log.error("获取内存信息失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取内存信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取性能指标
     */
    @GetMapping("/performance")
    // @Operation(summary = "获取性能指标", description = "获取Redis性能测试指标")
    public Result<Map<String, Object>> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = redisMonitoringService.getPerformanceMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            log.error("获取性能指标失败", e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "获取性能指标失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期缓存
     */
    @PostMapping("/cleanup")
    // @Operation(summary = "清理过期缓存", description = "清理指定模式的过期缓存")
    public Result<Long> cleanupExpiredCache(
            // @Parameter(description = "键模式，如 hello-quick:user:*") 
            @RequestParam(defaultValue = "hello-quick:*") String pattern) {
        try {
            long cleanedCount = redisMonitoringService.cleanupExpiredCache(pattern);
            return Result.success(cleanedCount, "清理了 " + cleanedCount + " 个过期缓存");
        } catch (Exception e) {
            log.error("清理过期缓存失败，pattern: {}", pattern, e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "清理过期缓存失败: " + e.getMessage());
        }
    }

    /**
     * 批量设置过期时间
     */
    @PostMapping("/expire")
    // @Operation(summary = "批量设置过期时间", description = "为指定模式的缓存批量设置过期时间")
    public Result<Long> batchExpire(
            // @Parameter(description = "键模式，如 hello-quick:user:*") 
            @RequestParam(defaultValue = "hello-quick:*") String pattern,
            // @Parameter(description = "过期时间（秒）") 
            @RequestParam long expireTime) {
        try {
            if (expireTime <= 0) {
                return Result.failure(ResultCode.PARAM_ERROR, "过期时间必须大于0");
            }
            
            long updatedCount = redisMonitoringService.batchExpire(pattern, expireTime);
            return Result.success(updatedCount, "更新了 " + updatedCount + " 个缓存的过期时间");
        } catch (Exception e) {
            log.error("批量设置过期时间失败，pattern: {}, expireTime: {}", pattern, expireTime, e);
            return Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "批量设置过期时间失败: " + e.getMessage());
        }
    }
}
