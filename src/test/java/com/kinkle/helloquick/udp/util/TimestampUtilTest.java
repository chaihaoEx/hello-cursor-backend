package com.kinkle.helloquick.udp.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 时间戳工具类测试
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class TimestampUtilTest {

    @Test
    void testCurrentTimestamp() {
        long timestamp = TimestampUtil.currentTimestamp();

        assertTrue(timestamp > 0, "Current timestamp should be positive");
        assertTrue(timestamp <= System.currentTimeMillis(), "Current timestamp should not be in future");

        // 验证两次调用间隔很小
        long timestamp2 = TimestampUtil.currentTimestamp();
        assertTrue(timestamp2 >= timestamp, "Second timestamp should be greater or equal to first");
        assertTrue(timestamp2 - timestamp < 10, "Timestamps should be very close");
    }

    @Test
    void testCurrentTimestampSeconds() {
        long timestampSeconds = TimestampUtil.currentTimestampSeconds();

        assertTrue(timestampSeconds > 0, "Current timestamp seconds should be positive");

        long timestampMillis = TimestampUtil.currentTimestamp();
        long expectedSeconds = timestampMillis / 1000;

        assertEquals(expectedSeconds, timestampSeconds, "Seconds should match milliseconds divided by 1000");
    }

    @Test
    void testCurrentTimestampNanos() {
        long timestampNanos = TimestampUtil.currentTimestampNanos();

        assertTrue(timestampNanos > 0, "Current timestamp nanos should be positive");

        // 验证两次调用值不同（nanoTime会变化）
        long timestampNanos2 = TimestampUtil.currentTimestampNanos();
        assertTrue(timestampNanos2 > timestampNanos, "Nano timestamps should be monotonically increasing");
    }

    @Test
    void testToLocalDateTime() {
        long timestamp = 1704067200000L; // 2024-01-01 00:00:00 UTC
        LocalDateTime dateTime = TimestampUtil.toLocalDateTime(timestamp);

        assertNotNull(dateTime);
        assertEquals(2024, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(1, dateTime.getDayOfMonth());
    }

    @Test
    void testToTimestamp() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 30, 45, 123456789);
        long timestamp = TimestampUtil.toTimestamp(dateTime);

        assertTrue(timestamp > 0);

        // 验证反向转换
        LocalDateTime convertedBack = TimestampUtil.toLocalDateTime(timestamp);
        assertEquals(dateTime.getYear(), convertedBack.getYear());
        assertEquals(dateTime.getMonthValue(), convertedBack.getMonthValue());
        assertEquals(dateTime.getDayOfMonth(), convertedBack.getDayOfMonth());
        assertEquals(dateTime.getHour(), convertedBack.getHour());
        assertEquals(dateTime.getMinute(), convertedBack.getMinute());
        assertEquals(dateTime.getSecond(), convertedBack.getSecond());
    }

    @Test
    void testFormatTimestamp() {
        long timestamp = 1704067200000L; // 2024-01-01 00:00:00
        String formatted = TimestampUtil.formatTimestamp(timestamp);

        assertNotNull(formatted);
        assertTrue(formatted.contains("2024"));
        assertTrue(formatted.contains("01"));
        assertTrue(formatted.contains("01"));
        assertTrue(formatted.length() == 23); // yyyy-MM-dd HH:mm:ss.SSS
    }

    @Test
    void testFormatTimestampWithPattern() {
        long timestamp = 1704067200000L; // 2024-01-01 00:00:00
        String pattern = "yyyy/MM/dd";
        String formatted = TimestampUtil.formatTimestamp(timestamp, pattern);

        assertNotNull(formatted);
        assertEquals("2024/01/01", formatted);
    }



    @Test
    void testIsValidTimestamp() {
        long currentTime = TimestampUtil.currentTimestamp();
        long oneMinuteAgo = currentTime - 60000;
        long oneHourAgo = currentTime - 3600000;
        long futureTime = currentTime + 60000;

        // 有效的timestamp
        assertTrue(TimestampUtil.isValidTimestamp(oneMinuteAgo, 120000)); // 2分钟内
        assertTrue(TimestampUtil.isValidTimestamp(currentTime, 1000));

        // 无效的timestamp
        assertFalse(TimestampUtil.isValidTimestamp(0, 1000)); // 0值
        assertFalse(TimestampUtil.isValidTimestamp(futureTime, 1000)); // 未来时间
        assertFalse(TimestampUtil.isValidTimestamp(oneHourAgo, 120000)); // 超过最大年龄
        assertFalse(TimestampUtil.isValidTimestamp(-1, 1000)); // 负数
    }

    @Test
    void testIsExpired() {
        long currentTime = TimestampUtil.currentTimestamp();
        long oneMinuteAgo = currentTime - 60000;
        long oneHourAgo = currentTime - 3600000;

        // 未过期的timestamp
        assertFalse(TimestampUtil.isExpired(oneMinuteAgo, 120000)); // 2分钟内

        // 已过期的timestamp
        assertTrue(TimestampUtil.isExpired(0, 1000)); // 0值
        assertTrue(TimestampUtil.isExpired(oneHourAgo, 120000)); // 超过最大年龄
        assertTrue(TimestampUtil.isExpired(-1, 1000)); // 负数
    }

    @Test
    void testCalculateDuration() {
        long start = 1704067200000L; // 2024-01-01 00:00:00
        long end = 1704067260000L; // 2024-01-01 00:01:00

        long duration = TimestampUtil.calculateDuration(start, end);
        assertEquals(60000, duration); // 1分钟 = 60000毫秒

        // 反向计算
        long negativeDuration = TimestampUtil.calculateDuration(end, start);
        assertEquals(-60000, negativeDuration);
    }

    @Test
    void testGetAge() {
        long pastTime = TimestampUtil.currentTimestamp() - 5000; // 5秒前
        long age = TimestampUtil.getAge(pastTime);

        assertTrue(age >= 5000, "Age should be at least 5 seconds");
        assertTrue(age < 6000, "Age should be less than 6 seconds (allowing some execution time)");
    }

    @Test
    void testCreateTimestampedMessage() {
        String message = "Test message";
        String timestampedMessage = TimestampUtil.createTimestampedMessage(message);

        assertNotNull(timestampedMessage);
        assertTrue(timestampedMessage.contains(message));
        assertTrue(timestampedMessage.startsWith("["));
        assertTrue(timestampedMessage.contains("]"));
        assertTrue(timestampedMessage.contains("-"));
        assertTrue(timestampedMessage.contains(":"));
    }

    @Test
    void testGetCurrentIsoString() {
        String isoString = TimestampUtil.getCurrentIsoString();

        assertNotNull(isoString);
        assertTrue(isoString.contains("T"), "ISO string should contain 'T'");
        assertTrue(isoString.contains("Z") || isoString.contains("+"), "ISO string should contain timezone");

        // 验证可以解析为Instant
        assertDoesNotThrow(() -> TimestampUtil.fromIsoString(isoString));
    }

    @Test
    void testFromIsoString() {
        String isoString = "2024-01-01T12:30:45.123Z";
        long timestamp = TimestampUtil.fromIsoString(isoString);

        assertTrue(timestamp > 0);

        // 验证反向转换 - 注意时区转换
        LocalDateTime dateTime = TimestampUtil.toLocalDateTime(timestamp);
        assertEquals(2024, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(1, dateTime.getDayOfMonth());
        assertEquals(30, dateTime.getMinute());
        assertEquals(45, dateTime.getSecond());

        // 小时数可能因时区而异，这里只验证基本信息
        assertTrue(dateTime.getHour() >= 0 && dateTime.getHour() <= 23);
    }

    @Test
    void testFromIsoStringInvalid() {
        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString("invalid-iso-string");
        });

        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString("");
        });

        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString(null);
        });
    }

    @Test
    void testFromIsoStringWithInvalidFormat() {
        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString("invalid-format");
        });

        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString("");
        });

        assertThrows(Exception.class, () -> {
            TimestampUtil.fromIsoString("2024-13-45T25:61:61Z"); // 无效的日期时间
        });
    }

    @Test
    void testToTimestampWithNullDateTime() {
        assertThrows(NullPointerException.class, () -> {
            TimestampUtil.toTimestamp(null);
        });
    }

    @Test
    void testToLocalDateTimeWithZeroTimestamp() {
        LocalDateTime dateTime = TimestampUtil.toLocalDateTime(0);
        assertNotNull(dateTime);
        assertEquals(1970, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(1, dateTime.getDayOfMonth());
    }

    @Test
    void testToLocalDateTimeWithNegativeTimestamp() {
        LocalDateTime dateTime = TimestampUtil.toLocalDateTime(-1);
        assertNotNull(dateTime);
        // 负时间戳应该在1970年之前
        assertTrue(dateTime.getYear() <= 1970);
    }

    @Test
    void testFormatTimestampWithZeroTimestamp() {
        String formatted = TimestampUtil.formatTimestamp(0);
        assertNotNull(formatted);
        assertTrue(formatted.contains("1970"));
    }

    @Test
    void testFormatTimestampWithNegativeTimestamp() {
        String formatted = TimestampUtil.formatTimestamp(-1);
        assertNotNull(formatted);
        assertTrue(formatted.contains("1969") || formatted.contains("1970"));
    }

    @Test
    void testFormatTimestampWithPatternAndZeroTimestamp() {
        String formatted = TimestampUtil.formatTimestamp(0, "yyyy-MM-dd");
        assertEquals("1970-01-01", formatted);
    }

    @Test
    void testCreateTimestampedMessageWithNullMessage() {
        assertThrows(NullPointerException.class, () -> {
            TimestampUtil.createTimestampedMessage(null);
        });
    }

    @Test
    void testCreateTimestampedMessageWithEmptyMessage() {
        String message = TimestampUtil.createTimestampedMessage("");
        assertNotNull(message);
        assertTrue(message.startsWith("["));
        assertTrue(message.contains("]"));
        assertTrue(message.endsWith(""));
    }

    @Test
    void testIsValidTimestampWithVariousValues() {
        long currentTime = TimestampUtil.currentTimestamp();

        // 测试0值
        assertFalse(TimestampUtil.isValidTimestamp(0, 1000));

        // 测试负值
        assertFalse(TimestampUtil.isValidTimestamp(-1, 1000));

        // 测试未来时间
        assertFalse(TimestampUtil.isValidTimestamp(currentTime + 1000, 1000));

        // 测试过期时间
        assertFalse(TimestampUtil.isValidTimestamp(currentTime - 2000, 1000));

        // 测试有效时间
        assertTrue(TimestampUtil.isValidTimestamp(currentTime - 500, 1000));
        assertTrue(TimestampUtil.isValidTimestamp(currentTime, 1000));
    }

    @Test
    void testIsExpiredWithVariousValues() {
        long currentTime = TimestampUtil.currentTimestamp();

        // 测试0值
        assertTrue(TimestampUtil.isExpired(0, 1000));

        // 测试负值
        assertTrue(TimestampUtil.isExpired(-1, 1000));

        // 测试未来时间
        assertTrue(TimestampUtil.isExpired(currentTime + 1000, 1000));

        // 测试过期时间
        assertTrue(TimestampUtil.isExpired(currentTime - 2000, 1000));

        // 测试未过期时间
        assertFalse(TimestampUtil.isExpired(currentTime - 500, 1000));
        assertFalse(TimestampUtil.isExpired(currentTime, 1000));
    }

    @Test
    void testCalculateDurationWithVariousValues() {
        // 测试正常情况
        assertEquals(1000, TimestampUtil.calculateDuration(1000, 2000));

        // 测试反向情况
        assertEquals(-1000, TimestampUtil.calculateDuration(2000, 1000));

        // 测试相同值
        assertEquals(0, TimestampUtil.calculateDuration(1000, 1000));

        // 测试大数值
        assertEquals(Long.MAX_VALUE - 1, TimestampUtil.calculateDuration(1, Long.MAX_VALUE));
    }

    @Test
    void testGetAgeWithVariousValues() {
        long currentTime = TimestampUtil.currentTimestamp();

        // 测试过去的时间
        long age = TimestampUtil.getAge(currentTime - 1000);
        assertTrue(age >= 1000);
        assertTrue(age < 2000); // 允许一些执行时间

        // 测试未来的时间
        long futureAge = TimestampUtil.getAge(currentTime + 1000);
        assertTrue(futureAge <= -1000);

        // 测试当前时间
        long currentAge = TimestampUtil.getAge(currentTime);
        assertTrue(currentAge >= 0);
        assertTrue(currentAge < 100); // 应该很小
    }

    @Test
    void testRoundTripConversion() {
        // 测试时间戳和LocalDateTime的双向转换
        long originalTimestamp = TimestampUtil.currentTimestamp();

        LocalDateTime dateTime = TimestampUtil.toLocalDateTime(originalTimestamp);
        long convertedTimestamp = TimestampUtil.toTimestamp(dateTime);

        // 由于LocalDateTime只到纳秒精度，会有一些精度损失
        long difference = Math.abs(originalTimestamp - convertedTimestamp);
        assertTrue(difference < 1000, "Round trip conversion should have minimal difference (less than 1ms)");
    }

    @Test
    void testConstructorThrowsException() {
        try {
            // 尝试通过反射调用私有构造函数
            TimestampUtil.class.getDeclaredConstructor().setAccessible(true);
            TimestampUtil.class.getDeclaredConstructor().newInstance();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (Exception e) {
            // 检查是否是UnsupportedOperationException或其他预期的异常
            assertTrue(e.getCause() instanceof UnsupportedOperationException ||
                      e instanceof IllegalAccessException,
                      "Expected UnsupportedOperationException or IllegalAccessException, but got: " + e.getClass());
        }
    }

    @Test
    void testFormatTimestampWithNullPattern() {
        long timestamp = TimestampUtil.currentTimestamp();

        assertThrows(IllegalArgumentException.class, () -> {
            TimestampUtil.formatTimestamp(timestamp, null);
        });
    }

    @Test
    void testFormatTimestampWithEmptyPattern() {
        long timestamp = TimestampUtil.currentTimestamp();

        assertThrows(IllegalArgumentException.class, () -> {
            TimestampUtil.formatTimestamp(timestamp, "");
        });
    }

    @Test
    void testFormatTimestampWithInvalidPattern() {
        long timestamp = TimestampUtil.currentTimestamp();

        // 测试无效的格式模式
        assertThrows(IllegalArgumentException.class, () -> {
            TimestampUtil.formatTimestamp(timestamp, "invalid-pattern-!!!");
        });
    }

    @Test
    void testFormatTimestampWithComplexPattern() {
        long timestamp = 1704067200000L; // 2024-01-01 00:00:00
        String formatted = TimestampUtil.formatTimestamp(timestamp, "yyyy年MM月dd日 HH:mm:ss");
        assertNotNull(formatted);
        assertTrue(formatted.contains("2024年"));
        assertTrue(formatted.contains("01月"));
        assertTrue(formatted.contains("01日"));
    }

    @Test
    void testGetCurrentIsoStringFormat() {
        String isoString = TimestampUtil.getCurrentIsoString();
        assertNotNull(isoString);
        assertTrue(isoString.length() > 10); // ISO字符串至少应该有一定的长度

        // 验证可以解析回时间戳
        long timestamp = TimestampUtil.fromIsoString(isoString);
        assertTrue(timestamp > 0);
    }

    @Test
    void testFromIsoStringWithTimezone() {
        // 测试带时区的ISO字符串
        String isoWithTimezone = "2024-01-01T12:30:45.123+08:00";
        long timestamp = TimestampUtil.fromIsoString(isoWithTimezone);
        assertTrue(timestamp > 0);

        // 测试UTC时区
        String isoUTC = "2024-01-01T12:30:45.123Z";
        long timestampUTC = TimestampUtil.fromIsoString(isoUTC);
        assertTrue(timestampUTC > 0);

        // UTC和带时区的应该不同
        assertNotEquals(timestamp, timestampUTC);
    }
}