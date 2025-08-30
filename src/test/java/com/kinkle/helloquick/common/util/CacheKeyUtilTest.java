package com.kinkle.helloquick.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * CacheKeyUtil单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("缓存键工具类测试")
class CacheKeyUtilTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTest {

        @Test
        @DisplayName("测试私有构造函数不能被实例化")
        void testPrivateConstructor() {
            // 使用反射测试私有构造函数
            try {
                Constructor<CacheKeyUtil> constructor = CacheKeyUtil.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                
                // 测试构造函数抛出异常
                Exception exception = assertThrows(InvocationTargetException.class, () -> {
                    constructor.newInstance();
                });
                
                // 检查是否是UnsupportedOperationException
                assertTrue(exception.getCause() instanceof UnsupportedOperationException);
                assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
                
            } catch (NoSuchMethodException e) {
                fail("构造函数不存在");
            }
        }
    }

    @Nested
    @DisplayName("用户相关缓存键测试")
    class UserCacheKeyTest {

        @Test
        @DisplayName("测试用户缓存键生成")
        void testGetUserKey() {
            Long userId = 123L;
            String expected = "hello-quick:user:123";
            String actual = CacheKeyUtil.getUserKey(userId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户名缓存键生成")
        void testGetUserByUsernameKey() {
            String username = "testuser";
            String expected = "hello-quick:user:username:testuser";
            String actual = CacheKeyUtil.getUserByUsernameKey(username);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户统计缓存键生成")
        void testGetUserStatsKey() {
            String statType = "login_count";
            String expected = "hello-quick:user-stats:login_count";
            String actual = CacheKeyUtil.getUserStatsKey(statType);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("会话相关缓存键测试")
    class SessionCacheKeyTest {

        @Test
        @DisplayName("测试会话缓存键生成")
        void testGetSessionKey() {
            String sessionId = "session123";
            String expected = "hello-quick:session:session123";
            String actual = CacheKeyUtil.getSessionKey(sessionId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户会话列表键生成")
        void testGetUserSessionsKey() {
            Long userId = 456L;
            String expected = "hello-quick:session:user:456";
            String actual = CacheKeyUtil.getUserSessionsKey(userId);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("限流相关缓存键测试")
    class RateLimitCacheKeyTest {

        @Test
        @DisplayName("测试API限流缓存键生成")
        void testGetRateLimitKey() {
            String identifier = "192.168.1.1";
            String resource = "login";
            String expected = "hello-quick:rate-limit:login:192.168.1.1";
            String actual = CacheKeyUtil.getRateLimitKey(identifier, resource);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试IP限流缓存键生成")
        void testGetIpRateLimitKey() {
            String ipAddress = "192.168.1.100";
            String endpoint = "/api/users";
            String expected = "hello-quick:rate-limit:ip:/api/users:192.168.1.100";
            String actual = CacheKeyUtil.getIpRateLimitKey(ipAddress, endpoint);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户限流缓存键生成")
        void testGetUserRateLimitKey() {
            Long userId = 789L;
            String endpoint = "/api/posts";
            String expected = "hello-quick:rate-limit:user:/api/posts:789";
            String actual = CacheKeyUtil.getUserRateLimitKey(userId, endpoint);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("验证码相关缓存键测试")
    class VerificationCacheKeyTest {

        @Test
        @DisplayName("测试验证码缓存键生成")
        void testGetVerificationCodeKey() {
            String type = "login";
            String identifier = "13800138000";
            String expected = "hello-quick:verification:login:13800138000";
            String actual = CacheKeyUtil.getVerificationCodeKey(type, identifier);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试短信验证码键生成")
        void testGetSmsCodeKey() {
            String phone = "13800138000";
            String type = "register";
            String expected = "hello-quick:verification:sms:register:13800138000";
            String actual = CacheKeyUtil.getSmsCodeKey(phone, type);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试邮箱验证码键生成")
        void testGetEmailCodeKey() {
            String email = "test@example.com";
            String type = "reset-password";
            String expected = "hello-quick:verification:email:reset-password:test@example.com";
            String actual = CacheKeyUtil.getEmailCodeKey(email, type);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("测试相关缓存键测试")
    class TestCacheKeyTest {

        @Test
        @DisplayName("测试测试缓存键生成")
        void testGetTestKey() {
            String key = "test123";
            String expected = "hello-quick:test:test123";
            String actual = CacheKeyUtil.getTestKey(key);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试Ping测试键生成")
        void testGetPingTestKey() {
            String expected = "hello-quick:test:ping";
            String actual = CacheKeyUtil.getPingTestKey();
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试统计测试键生成")
        void testGetStatsTestKey() {
            String statType = "performance";
            String expected = "hello-quick:test:stats:performance";
            String actual = CacheKeyUtil.getStatsTestKey(statType);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("业务相关缓存键测试")
    class BusinessCacheKeyTest {

        @Test
        @DisplayName("测试角色缓存键生成")
        void testGetRoleKey() {
            Long roleId = 1L;
            String expected = "hello-quick:role:1";
            String actual = CacheKeyUtil.getRoleKey(roleId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户角色缓存键生成")
        void testGetUserRoleKey() {
            Long userId = 100L;
            String expected = "hello-quick:user-role:100";
            String actual = CacheKeyUtil.getUserRoleKey(userId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试权限缓存键生成")
        void testGetPermissionKey() {
            Long userId = 200L;
            String expected = "hello-quick:permission:200";
            String actual = CacheKeyUtil.getPermissionKey(userId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试配置缓存键生成")
        void testGetConfigKey() {
            String configKey = "app.version";
            String expected = "hello-quick:config:app.version";
            String actual = CacheKeyUtil.getConfigKey(configKey);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("Spring Cache注解支持测试")
    class SpringCacheKeyTest {

        @Test
        @DisplayName("测试缓存键表达式生成")
        void testGetCacheKeyExpression() {
            String keyPattern = "user:#{userId}";
            String expected = "hello-quick:cache:user:#{userId}";
            String actual = CacheKeyUtil.getCacheKeyExpression(keyPattern);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户缓存键（Spring Cache）")
        void testGetUserCacheKey() {
            Long userId = 300L;
            String expected = "hello-quick:cache:user:300";
            String actual = CacheKeyUtil.getUserCacheKey(userId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户统计缓存键（Spring Cache）")
        void testGetUserStatsCacheKey() {
            String statType = "active_users";
            String expected = "hello-quick:cache:user-stats:active_users";
            String actual = CacheKeyUtil.getUserStatsCacheKey(statType);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试会话缓存键（Spring Cache）")
        void testGetSessionCacheKey() {
            String sessionId = "session456";
            String expected = "hello-quick:cache:session:session456";
            String actual = CacheKeyUtil.getSessionCacheKey(sessionId);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试限流缓存键（Spring Cache）")
        void testGetRateLimitCacheKey() {
            String identifier = "user123";
            String resource = "api";
            String expected = "hello-quick:cache:rate-limit:api:user123";
            String actual = CacheKeyUtil.getRateLimitCacheKey(identifier, resource);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("缓存键模式常量测试")
    class CacheKeyPatternTest {

        @Test
        @DisplayName("测试用户缓存键模式常量")
        void testUserCacheKeyPattern() {
            String expected = "user::#{#id}";
            String actual = CacheKeyUtil.USER_CACHE_KEY_PATTERN;
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户名缓存键模式常量")
        void testUsernameCacheKeyPattern() {
            String expected = "user::username::#{#username}";
            String actual = CacheKeyUtil.USERNAME_CACHE_KEY_PATTERN;
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试用户统计缓存键模式常量")
        void testUserStatsCacheKeyPattern() {
            String expected = "user-stats::#{#statType}";
            String actual = CacheKeyUtil.USER_STATS_CACHE_KEY_PATTERN;
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试会话缓存键模式常量")
        void testSessionCacheKeyPattern() {
            String expected = "session::#{#sessionId}";
            String actual = CacheKeyUtil.SESSION_CACHE_KEY_PATTERN;
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试限流缓存键模式常量")
        void testRateLimitCacheKeyPattern() {
            String expected = "rate-limit::#{#resource}::#{#identifier}";
            String actual = CacheKeyUtil.RATE_LIMIT_CACHE_KEY_PATTERN;
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("工具方法测试")
    class UtilityMethodTest {

        @Test
        @DisplayName("测试构建缓存键 - 正常情况")
        void testBuildKeyNormal() {
            String[] parts = {"user", "123", "profile"};
            String expected = "hello-quick:user:123:profile";
            String actual = CacheKeyUtil.buildKey(parts);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试构建缓存键 - 空数组")
        void testBuildKeyEmptyArray() {
            String[] parts = {};
            String expected = "hello-quick";
            String actual = CacheKeyUtil.buildKey(parts);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试构建缓存键 - null数组")
        void testBuildKeyNullArray() {
            String[] parts = null;
            String expected = "hello-quick";
            String actual = CacheKeyUtil.buildKey(parts);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试构建缓存键 - 包含null和空字符串")
        void testBuildKeyWithNullAndEmpty() {
            String[] parts = {"user", null, "", "123", null};
            String expected = "hello-quick:user:123";
            String actual = CacheKeyUtil.buildKey(parts);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试构建缓存键 - 单个部分")
        void testBuildKeySinglePart() {
            String[] parts = {"user"};
            String expected = "hello-quick:user";
            String actual = CacheKeyUtil.buildKey(parts);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试验证缓存键格式 - 有效键")
        void testIsValidKeyValid() {
            String key = "hello-quick:user:123";
            assertTrue(CacheKeyUtil.isValidKey(key));
        }

        @Test
        @DisplayName("测试验证缓存键格式 - 无效键（null）")
        void testIsValidKeyNull() {
            String key = null;
            assertFalse(CacheKeyUtil.isValidKey(key));
        }

        @Test
        @DisplayName("测试验证缓存键格式 - 无效键（不匹配前缀）")
        void testIsValidKeyInvalidPrefix() {
            String key = "other-app:user:123";
            assertFalse(CacheKeyUtil.isValidKey(key));
        }

        @Test
        @DisplayName("测试验证缓存键格式 - 无效键（只有前缀）")
        void testIsValidKeyOnlyPrefix() {
            String key = "hello-quick";
            assertFalse(CacheKeyUtil.isValidKey(key));
        }

        @Test
        @DisplayName("测试验证缓存键格式 - 无效键（前缀加分隔符）")
        void testIsValidKeyPrefixWithSeparator() {
            String key = "hello-quick:";
            assertFalse(CacheKeyUtil.isValidKey(key));
        }

        @Test
        @DisplayName("测试获取缓存键命名空间 - 有效键")
        void testGetNamespaceValid() {
            String key = "hello-quick:user:123";
            String expected = "user";
            String actual = CacheKeyUtil.getNamespace(key);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试获取缓存键命名空间 - 单层命名空间")
        void testGetNamespaceSingleLevel() {
            String key = "hello-quick:config";
            String expected = "config";
            String actual = CacheKeyUtil.getNamespace(key);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试获取缓存键命名空间 - 无效键")
        void testGetNamespaceInvalid() {
            String key = "invalid-key";
            String actual = CacheKeyUtil.getNamespace(key);
            assertNull(actual);
        }

        @Test
        @DisplayName("测试获取缓存键命名空间 - null键")
        void testGetNamespaceNull() {
            String key = null;
            String actual = CacheKeyUtil.getNamespace(key);
            assertNull(actual);
        }

        @Test
        @DisplayName("测试获取缓存键命名空间 - 只有前缀")
        void testGetNamespaceOnlyPrefix() {
            String key = "hello-quick";
            String actual = CacheKeyUtil.getNamespace(key);
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("测试空字符串参数")
        void testEmptyStringParameters() {
            String result = CacheKeyUtil.getUserByUsernameKey("");
            assertEquals("hello-quick:user:username:", result);
        }

        @Test
        @DisplayName("测试特殊字符参数")
        void testSpecialCharacterParameters() {
            String username = "user@domain.com";
            String result = CacheKeyUtil.getUserByUsernameKey(username);
            assertEquals("hello-quick:user:username:user@domain.com", result);
        }

        @Test
        @DisplayName("测试长字符串参数")
        void testLongStringParameters() {
            String longString = "a".repeat(1000);
            String result = CacheKeyUtil.getTestKey(longString);
            assertEquals("hello-quick:test:" + longString, result);
        }

        @Test
        @DisplayName("测试零值ID")
        void testZeroId() {
            Long zeroId = 0L;
            String result = CacheKeyUtil.getUserKey(zeroId);
            assertEquals("hello-quick:user:0", result);
        }

        @Test
        @DisplayName("测试负数ID")
        void testNegativeId() {
            Long negativeId = -1L;
            String result = CacheKeyUtil.getUserKey(negativeId);
            assertEquals("hello-quick:user:-1", result);
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTest {

        @Test
        @DisplayName("测试大量键生成性能")
        void testBulkKeyGenerationPerformance() {
            long startTime = System.currentTimeMillis();
            
            // 生成10000个键
            for (int i = 0; i < 10000; i++) {
                CacheKeyUtil.getUserKey((long) i);
                CacheKeyUtil.getUserByUsernameKey("user" + i);
                CacheKeyUtil.getSessionKey("session" + i);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 确保在合理时间内完成（100ms内）
            assertTrue(duration < 100, "键生成性能测试失败，耗时: " + duration + "ms");
        }
    }
}
