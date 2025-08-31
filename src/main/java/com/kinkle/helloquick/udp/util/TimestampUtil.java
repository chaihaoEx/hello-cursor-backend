package com.kinkle.helloquick.udp.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳工具类
 * 
 * <p>提供时间戳相关的工具方法，包括时间戳生成、格式化、转换等。</p>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
public final class TimestampUtil {
    
    /**
     * 默认时区
     */
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    
    /**
     * 默认日期时间格式
     */
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 私有构造函数，防止实例化
     */
    private TimestampUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 获取当前时间戳（毫秒）
     * 
     * @return 当前时间戳（毫秒）
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取当前时间戳（秒）
     * 
     * @return 当前时间戳（秒）
     */
    public static long currentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 获取当前时间戳（纳秒）
     * 
     * @return 当前时间戳（纳秒）
     */
    public static long currentTimestampNanos() {
        return System.nanoTime();
    }
    
    /**
     * 将时间戳转换为LocalDateTime
     * 
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE);
    }
    
    /**
     * 将LocalDateTime转换为时间戳
     * 
     * @param dateTime LocalDateTime对象
     * @return 时间戳（毫秒）
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }
    
    /**
     * 格式化时间戳为字符串
     * 
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的时间字符串
     */
    public static String formatTimestamp(long timestamp) {
        return toLocalDateTime(timestamp).format(DEFAULT_FORMATTER);
    }
    
    /**
     * 格式化时间戳为字符串（自定义格式）
     * 
     * @param timestamp 时间戳（毫秒）
     * @param pattern 日期格式模式
     * @return 格式化后的时间字符串
     */
    public static String formatTimestamp(long timestamp, String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return toLocalDateTime(timestamp).format(formatter);
    }
    
    /**
     * 检查时间戳是否在有效范围内
     * 
     * @param timestamp 时间戳（毫秒）
     * @param maxAge 最大年龄（毫秒）
     * @return 是否在有效范围内
     */
    public static boolean isValidTimestamp(long timestamp, long maxAge) {
        long currentTime = currentTimestamp();
        return timestamp > 0 && timestamp <= currentTime && (currentTime - timestamp) <= maxAge;
    }
    
    /**
     * 检查时间戳是否过期
     * 
     * @param timestamp 时间戳（毫秒）
     * @param maxAge 最大年龄（毫秒）
     * @return 是否过期
     */
    public static boolean isExpired(long timestamp, long maxAge) {
        return !isValidTimestamp(timestamp, maxAge);
    }
    
    /**
     * 计算时间差（毫秒）
     * 
     * @param startTimestamp 开始时间戳
     * @param endTimestamp 结束时间戳
     * @return 时间差（毫秒）
     */
    public static long calculateDuration(long startTimestamp, long endTimestamp) {
        return endTimestamp - startTimestamp;
    }
    
    /**
     * 获取时间戳的年龄（毫秒）
     * 
     * @param timestamp 时间戳（毫秒）
     * @return 年龄（毫秒）
     */
    public static long getAge(long timestamp) {
        return currentTimestamp() - timestamp;
    }
    
    /**
     * 创建带时间戳的日志消息
     * 
     * @param message 原始消息
     * @return 带时间戳的消息
     */
    public static String createTimestampedMessage(String message) {
        if (message == null) {
            throw new NullPointerException("Message cannot be null");
        }
        return String.format("[%s] %s", formatTimestamp(currentTimestamp()), message);
    }
    
    /**
     * 获取当前时间的ISO格式字符串
     * 
     * @return ISO格式的时间字符串
     */
    public static String getCurrentIsoString() {
        return Instant.now().toString();
    }
    
    /**
     * 将ISO格式字符串转换为时间戳
     * 
     * @param isoString ISO格式的时间字符串
     * @return 时间戳（毫秒）
     */
    public static long fromIsoString(String isoString) {
        return Instant.parse(isoString).toEpochMilli();
    }
}
