package com.kinkle.helloquick.udp.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 协议类型枚举测试类
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class ProtocolTypeTest {

    @Test
    void testSystemProtocolValues() {
        // 测试系统协议的协议号和分类
        assertEquals(ProtocolConstants.SystemProtocols.HEARTBEAT, ProtocolType.HEARTBEAT.getProtocolId());
        assertEquals("心跳检测", ProtocolType.HEARTBEAT.getName());
        assertEquals(ProtocolType.ProtocolCategory.SYSTEM, ProtocolType.HEARTBEAT.getCategory());

        assertEquals(ProtocolConstants.SystemProtocols.AUTH_REQUEST, ProtocolType.AUTH_REQUEST.getProtocolId());
        assertEquals("认证请求", ProtocolType.AUTH_REQUEST.getName());
        assertEquals(ProtocolType.ProtocolCategory.SYSTEM, ProtocolType.AUTH_REQUEST.getCategory());

        assertEquals(ProtocolConstants.SystemProtocols.AUTH_RESPONSE, ProtocolType.AUTH_RESPONSE.getProtocolId());
        assertEquals("认证响应", ProtocolType.AUTH_RESPONSE.getName());
        assertEquals(ProtocolType.ProtocolCategory.SYSTEM, ProtocolType.AUTH_RESPONSE.getCategory());

        assertEquals(ProtocolConstants.SystemProtocols.ERROR_RESPONSE, ProtocolType.ERROR_RESPONSE.getProtocolId());
        assertEquals("错误响应", ProtocolType.ERROR_RESPONSE.getName());
        assertEquals(ProtocolType.ProtocolCategory.SYSTEM, ProtocolType.ERROR_RESPONSE.getCategory());

        assertEquals(ProtocolConstants.SystemProtocols.ACK, ProtocolType.ACK.getProtocolId());
        assertEquals("确认响应", ProtocolType.ACK.getName());
        assertEquals(ProtocolType.ProtocolCategory.SYSTEM, ProtocolType.ACK.getCategory());
    }

    @Test
    void testBusinessProtocolValues() {
        // 测试业务协议的协议号和分类
        assertEquals(ProtocolConstants.BusinessProtocols.USER_LOGIN, ProtocolType.USER_LOGIN.getProtocolId());
        assertEquals("用户登录", ProtocolType.USER_LOGIN.getName());
        assertEquals(ProtocolType.ProtocolCategory.BUSINESS, ProtocolType.USER_LOGIN.getCategory());

        assertEquals(ProtocolConstants.BusinessProtocols.USER_LOGOUT, ProtocolType.USER_LOGOUT.getProtocolId());
        assertEquals("用户登出", ProtocolType.USER_LOGOUT.getName());
        assertEquals(ProtocolType.ProtocolCategory.BUSINESS, ProtocolType.USER_LOGOUT.getCategory());

        assertEquals(ProtocolConstants.BusinessProtocols.DATA_SYNC, ProtocolType.DATA_SYNC.getProtocolId());
        assertEquals("数据同步", ProtocolType.DATA_SYNC.getName());
        assertEquals(ProtocolType.ProtocolCategory.BUSINESS, ProtocolType.DATA_SYNC.getCategory());

        assertEquals(ProtocolConstants.BusinessProtocols.FILE_UPLOAD, ProtocolType.FILE_UPLOAD.getProtocolId());
        assertEquals("文件上传", ProtocolType.FILE_UPLOAD.getName());
        assertEquals(ProtocolType.ProtocolCategory.BUSINESS, ProtocolType.FILE_UPLOAD.getCategory());
    }

    @Test
    void testFromProtocolIdWithValidIds() {
        // 测试有效的协议号查找
        assertEquals(ProtocolType.HEARTBEAT, ProtocolType.fromProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT));
        assertEquals(ProtocolType.AUTH_REQUEST, ProtocolType.fromProtocolId(ProtocolConstants.SystemProtocols.AUTH_REQUEST));
        assertEquals(ProtocolType.USER_LOGIN, ProtocolType.fromProtocolId(ProtocolConstants.BusinessProtocols.USER_LOGIN));
        assertEquals(ProtocolType.DATA_SYNC, ProtocolType.fromProtocolId(ProtocolConstants.BusinessProtocols.DATA_SYNC));
    }

    @Test
    void testFromProtocolIdWithInvalidIds() {
        // 测试无效的协议号查找
        assertNull(ProtocolType.fromProtocolId((short) 0x0000)); // 无效协议号
        assertNull(ProtocolType.fromProtocolId((short) 0xFFFF)); // 超出范围
        assertNull(ProtocolType.fromProtocolId((short) 0x8000)); // 超出范围
    }

    @Test
    void testIsSystemProtocol() {
        // 测试系统协议范围检查
        assertTrue(ProtocolType.isSystemProtocol(ProtocolConstants.SystemProtocols.HEARTBEAT));
        assertTrue(ProtocolType.isSystemProtocol(ProtocolConstants.SystemProtocols.AUTH_REQUEST));
        assertTrue(ProtocolType.isSystemProtocol((short) 0x00FF)); // 范围上限

        assertFalse(ProtocolType.isSystemProtocol((short) 0x0000)); // 下限外
        assertFalse(ProtocolType.isSystemProtocol((short) 0x0100)); // 上限外
        assertFalse(ProtocolType.isSystemProtocol(ProtocolConstants.BusinessProtocols.USER_LOGIN));
    }

    @Test
    void testIsBusinessProtocol() {
        // 测试业务协议范围检查
        assertTrue(ProtocolType.isBusinessProtocol(ProtocolConstants.BusinessProtocols.USER_LOGIN));
        assertTrue(ProtocolType.isBusinessProtocol(ProtocolConstants.BusinessProtocols.DATA_SYNC));
        assertTrue(ProtocolType.isBusinessProtocol((short) 0x0FFF)); // 范围上限

        assertFalse(ProtocolType.isBusinessProtocol((short) 0x00FF)); // 下限外
        assertFalse(ProtocolType.isBusinessProtocol((short) 0x1000)); // 上限外
        assertFalse(ProtocolType.isBusinessProtocol(ProtocolConstants.SystemProtocols.HEARTBEAT));
    }

    @Test
    void testIsExtensionProtocol() {
        // 测试扩展协议范围检查
        assertTrue(ProtocolType.isExtensionProtocol((short) 0x1000)); // 范围下限
        assertTrue(ProtocolType.isExtensionProtocol((short) 0x7FFF)); // 范围上限
        assertTrue(ProtocolType.isExtensionProtocol((short) 0x2000)); // 范围中值

        assertFalse(ProtocolType.isExtensionProtocol((short) 0x0FFF)); // 下限外
        assertFalse(ProtocolType.isExtensionProtocol((short) 0x8000)); // 上限外
        assertFalse(ProtocolType.isExtensionProtocol(ProtocolConstants.SystemProtocols.HEARTBEAT));
    }

    @ParameterizedTest
    @ValueSource(shorts = {0x0001, 0x0002, 0x0003, 0x0004, 0x0005})
    void testAllSystemProtocolIdsAreSystemProtocol(short protocolId) {
        // 验证所有枚举中的系统协议号都在系统协议范围内
        assertTrue(ProtocolType.isSystemProtocol(protocolId));
    }

    @ParameterizedTest
    @ValueSource(shorts = {0x0100, 0x0101, 0x0102, 0x0103})
    void testAllBusinessProtocolIdsAreBusinessProtocol(short protocolId) {
        // 验证所有枚举中的业务协议号都在业务协议范围内
        assertTrue(ProtocolType.isBusinessProtocol(protocolId));
    }

    @Test
    void testProtocolCategoryValues() {
        // 测试协议分类枚举值
        assertEquals("系统协议", ProtocolType.ProtocolCategory.SYSTEM.getDescription());
        assertEquals("业务协议", ProtocolType.ProtocolCategory.BUSINESS.getDescription());
        assertEquals("扩展协议", ProtocolType.ProtocolCategory.EXTENSION.getDescription());
    }

    @Test
    void testAllEnumsHaveUniqueProtocolIds() {
        // 验证所有枚举值有唯一的协议号
        ProtocolType[] values = ProtocolType.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i].getProtocolId(), values[j].getProtocolId(),
                    "Protocol IDs must be unique: " + values[i] + " and " + values[j]);
            }
        }
    }

    @Test
    void testAllEnumsHaveNonNullNames() {
        // 验证所有枚举值都有非空名称
        for (ProtocolType type : ProtocolType.values()) {
            assertNotNull(type.getName(), "Protocol name should not be null for " + type);
            assertFalse(type.getName().trim().isEmpty(), "Protocol name should not be empty for " + type);
        }
    }

    @Test
    void testAllEnumsHaveNonNullCategories() {
        // 验证所有枚举值都有非空分类
        for (ProtocolType type : ProtocolType.values()) {
            assertNotNull(type.getCategory(), "Protocol category should not be null for " + type);
        }
    }

    @Test
    void testValuesMethod() {
        // 测试values()方法返回所有枚举值
        ProtocolType[] values = ProtocolType.values();
        assertNotNull(values);
        assertTrue(values.length > 0, "Should have at least one protocol type");

        // 验证包含预期的枚举值
        boolean hasHeartbeat = false;
        boolean hasUserLogin = false;
        for (ProtocolType type : values) {
            if (type == ProtocolType.HEARTBEAT) {
                hasHeartbeat = true;
            }
            if (type == ProtocolType.USER_LOGIN) {
                hasUserLogin = true;
            }
        }
        assertTrue(hasHeartbeat, "Should contain HEARTBEAT");
        assertTrue(hasUserLogin, "Should contain USER_LOGIN");
    }

    @Test
    void testValueOfMethod() {
        // 测试valueOf方法
        assertEquals(ProtocolType.HEARTBEAT, ProtocolType.valueOf("HEARTBEAT"));
        assertEquals(ProtocolType.USER_LOGIN, ProtocolType.valueOf("USER_LOGIN"));
        assertEquals(ProtocolType.DATA_SYNC, ProtocolType.valueOf("DATA_SYNC"));
    }

    @Test
    void testValueOfMethodWithInvalidName() {
        // 测试valueOf方法使用无效名称
        assertThrows(IllegalArgumentException.class, () -> ProtocolType.valueOf("INVALID_PROTOCOL"));
    }

    @Test
    void testToStringMethod() {
        // 测试toString方法
        for (ProtocolType type : ProtocolType.values()) {
            String toString = type.toString();
            assertNotNull(toString);
            assertFalse(toString.trim().isEmpty());
            assertTrue(toString.contains(type.name()), "toString should contain enum name");
        }
    }

    @Test
    void testOrdinalMethod() {
        // 测试ordinal方法
        for (ProtocolType type : ProtocolType.values()) {
            int ordinal = type.ordinal();
            assertTrue(ordinal >= 0, "Ordinal should be non-negative");
            assertTrue(ordinal < ProtocolType.values().length, "Ordinal should be less than total count");
        }
    }

    @Test
    void testNameMethod() {
        // 测试name方法
        assertEquals("HEARTBEAT", ProtocolType.HEARTBEAT.name());
        assertEquals("USER_LOGIN", ProtocolType.USER_LOGIN.name());
        assertEquals("DATA_SYNC", ProtocolType.DATA_SYNC.name());
    }
}
