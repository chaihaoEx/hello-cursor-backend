package com.kinkle.helloquick.udp.protocol;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProtocolConstants 单元测试
 *
 * <p>只测试必要的部分：私有构造函数和关键常量关系</p>
 *
 * @author kinkle
 * @since 2025-08-31
 */
class ProtocolConstantsTest {

    /**
     * 测试私有构造函数是否正确抛出异常
     */
    @Test
    void testPrivateConstructor() {
        try {
            // 使用反射尝试实例化
            var constructor = ProtocolConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (Exception e) {
            // 检查是否是反射异常包装的 UnsupportedOperationException
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                assertTrue(cause instanceof UnsupportedOperationException);
                assertEquals("Utility class", cause.getMessage());
            } else {
                fail("Unexpected exception type: " + e.getClass().getName());
            }
        }
    }

    /**
     * 测试协议号范围关系是否正确
     */
    @Test
    void testProtocolRanges() {
        // 系统协议范围
        assertTrue(ProtocolConstants.ProtocolRanges.SYSTEM_START < ProtocolConstants.ProtocolRanges.SYSTEM_END);

        // 业务协议范围
        assertTrue(ProtocolConstants.ProtocolRanges.BUSINESS_START < ProtocolConstants.ProtocolRanges.BUSINESS_END);

        // 扩展协议范围
        assertTrue(ProtocolConstants.ProtocolRanges.EXTENSION_START < ProtocolConstants.ProtocolRanges.EXTENSION_END);

        // 确保范围不重叠
        assertTrue(ProtocolConstants.ProtocolRanges.SYSTEM_END < ProtocolConstants.ProtocolRanges.BUSINESS_START);
        assertTrue(ProtocolConstants.ProtocolRanges.BUSINESS_END < ProtocolConstants.ProtocolRanges.EXTENSION_START);
    }

    /**
     * 测试包头长度计算是否正确
     */
    @Test
    void testHeaderLengthCalculation() {
        int expectedLength = ProtocolConstants.HeaderLengths.MAGIC
                           + ProtocolConstants.HeaderLengths.VERSION
                           + ProtocolConstants.HeaderLengths.PROTOCOL_ID
                           + ProtocolConstants.HeaderLengths.ENCODING
                           + ProtocolConstants.HeaderLengths.ENCRYPTION
                           + ProtocolConstants.HeaderLengths.BODY_LENGTH
                           + ProtocolConstants.HeaderLengths.TIMESTAMP
                           + ProtocolConstants.HeaderLengths.RESERVED;

        assertEquals(ProtocolConstants.HEADER_LENGTH, expectedLength);
    }

    /**
     * 测试最大包体长度是否合理
     */
    @Test
    void testMaxBodyLength() {
        assertTrue(ProtocolConstants.MAX_BODY_LENGTH > 0);
        assertTrue(ProtocolConstants.MAX_BODY_LENGTH >= ProtocolConstants.MIN_BODY_LENGTH);
    }
}
