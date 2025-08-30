package com.kinkle.helloquick.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User实体类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("User 用户实体类测试")
class UserTest {

    @Nested
    @DisplayName("构造函数和Builder测试")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("无参构造函数测试")
        void testNoArgsConstructor() {
            // When
            User user = new User();

            // Then
            assertAll(
                () -> assertNull(user.getId()),
                () -> assertNull(user.getUsername()),
                () -> assertNull(user.getEmail()),
                () -> assertNull(user.getPassword()),
                () -> assertNull(user.getFullName()),
                () -> assertNull(user.getPhone()),
                () -> assertNull(user.getCreatedAt()),
                () -> assertNull(user.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("全参构造函数测试")
        void testAllArgsConstructor() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            User user = new User(1L, "testuser", "test@example.com", "password", 
                               "Test User", "13800138000", 1, now, now);

            // Then
            assertAll(
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("testuser", user.getUsername()),
                () -> assertEquals("test@example.com", user.getEmail()),
                () -> assertEquals("password", user.getPassword()),
                () -> assertEquals("Test User", user.getFullName()),
                () -> assertEquals("13800138000", user.getPhone()),
                () -> assertEquals(1, user.getStatus()),
                () -> assertEquals(now, user.getCreatedAt()),
                () -> assertEquals(now, user.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("Builder模式测试")
        void testBuilderPattern() {
            // When
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("password")
                    .fullName("Test User")
                    .phone("13800138000")
                    .build();

            // Then
            assertAll(
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("testuser", user.getUsername()),
                () -> assertEquals("test@example.com", user.getEmail()),
                () -> assertEquals("password", user.getPassword()),
                () -> assertEquals("Test User", user.getFullName()),
                () -> assertEquals("13800138000", user.getPhone()),
                () -> assertEquals(1, user.getStatus()), // 默认值
                () -> assertNull(user.getCreatedAt()),
                () -> assertNull(user.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("Builder默认值测试")
        void testBuilderDefaults() {
            // When
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .build();

            // Then
            assertEquals(1, user.getStatus()); // 默认状态为1（启用）
        }
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password("password")
                    .status(1)
                    .build();
        }

        @Test
        @DisplayName("isEnabled方法测试")
        void testIsEnabled() {
            // 启用状态测试
            user.setStatus(1);
            assertTrue(user.isEnabled());

            // 禁用状态测试
            user.setStatus(0);
            assertFalse(user.isEnabled());

            // null状态测试
            user.setStatus(null);
            assertFalse(user.isEnabled());

            // 其他数值测试
            user.setStatus(2);
            assertFalse(user.isEnabled());
        }

        @Test
        @DisplayName("enable方法测试")
        void testEnable() {
            // Given
            user.setStatus(0);

            // When
            user.enable();

            // Then
            assertAll(
                () -> assertEquals(1, user.getStatus()),
                () -> assertTrue(user.isEnabled())
            );
        }

        @Test
        @DisplayName("disable方法测试")
        void testDisable() {
            // Given
            user.setStatus(1);

            // When
            user.disable();

            // Then
            assertAll(
                () -> assertEquals(0, user.getStatus()),
                () -> assertFalse(user.isEnabled())
            );
        }

        @Test
        @DisplayName("状态切换测试")
        void testStatusToggle() {
            // 初始状态：启用
            assertTrue(user.isEnabled());

            // 禁用
            user.disable();
            assertFalse(user.isEnabled());

            // 重新启用
            user.enable();
            assertTrue(user.isEnabled());
        }
    }

    @Nested
    @DisplayName("数据验证测试")
    class DataValidationTests {

        @Test
        @DisplayName("字段长度限制测试")
        void testFieldLengths() {
            // Given
            User user = User.builder()
                    .username("a".repeat(50))    // 最大长度
                    .email("a".repeat(90) + "@test.com") // 接近最大长度
                    .password("a".repeat(255))   // 最大长度
                    .fullName("a".repeat(100))   // 最大长度
                    .phone("a".repeat(20))       // 最大长度
                    .build();

            // Then
            assertAll(
                () -> assertEquals(50, user.getUsername().length()),
                () -> assertTrue(user.getEmail().length() <= 100),
                () -> assertEquals(255, user.getPassword().length()),
                () -> assertEquals(100, user.getFullName().length()),
                () -> assertEquals(20, user.getPhone().length())
            );
        }

        @Test
        @DisplayName("可空字段测试")
        void testNullableFields() {
            // Given & When
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .fullName(null)  // 可空
                    .phone(null)     // 可空
                    .build();

            // Then
            assertAll(
                () -> assertNotNull(user.getUsername()),
                () -> assertNotNull(user.getEmail()),
                () -> assertNotNull(user.getPassword()),
                () -> assertNull(user.getFullName()),
                () -> assertNull(user.getPhone())
            );
        }
    }

    @Nested
    @DisplayName("equals和hashCode测试")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("相同对象equals测试")
        void testEqualsWithSameObject() {
            // Given
            User user = User.builder()
                    .id(1L)
                    .username("test")
                    .build();

            // When & Then
            assertEquals(user, user);
            assertEquals(user.hashCode(), user.hashCode());
        }

        @Test
        @DisplayName("相同内容equals测试")
        void testEqualsWithSameContent() {
            // Given
            User user1 = User.builder()
                    .id(1L)
                    .username("test")
                    .email("test@example.com")
                    .build();

            User user2 = User.builder()
                    .id(1L)
                    .username("test")
                    .email("test@example.com")
                    .build();

            // When & Then
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("不同内容equals测试")
        void testEqualsWithDifferentContent() {
            // Given
            User user1 = User.builder().id(1L).username("test1").build();
            User user2 = User.builder().id(2L).username("test2").build();

            // When & Then
            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("null对象equals测试")
        void testEqualsWithNull() {
            // Given
            User user = User.builder().id(1L).build();

            // When & Then
            assertNotEquals(user, null);
        }
    }

    @Nested
    @DisplayName("toString测试")
    class ToStringTests {

        @Test
        @DisplayName("toString不包含密码")
        void testToStringExcludesPassword() {
            // Given
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("secret123")
                    .build();

            // When
            String toString = user.toString();

            // Then
            assertAll(
                () -> assertTrue(toString.contains("test")),
                () -> assertTrue(toString.contains("test@example.com")),
                () -> assertFalse(toString.contains("secret123"), "toString不应包含密码")
            );
        }
    }
}
