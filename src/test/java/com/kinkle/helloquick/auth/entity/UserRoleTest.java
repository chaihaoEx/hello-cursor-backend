package com.kinkle.helloquick.auth.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRole实体类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("UserRole 用户角色关联实体类测试")
class UserRoleTest {

    @Nested
    @DisplayName("构造函数和Builder测试")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("无参构造函数测试")
        void testNoArgsConstructor() {
            // When
            UserRole userRole = new UserRole();

            // Then
            assertAll(
                () -> assertNull(userRole.getId()),
                () -> assertNull(userRole.getUserId()),
                () -> assertNull(userRole.getRoleId()),
                () -> assertNull(userRole.getCreatedAt())
            );
        }

        @Test
        @DisplayName("全参构造函数测试")
        void testAllArgsConstructor() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            UserRole userRole = new UserRole(1L, 100L, 200L, now);

            // Then
            assertAll(
                () -> assertEquals(1L, userRole.getId()),
                () -> assertEquals(100L, userRole.getUserId()),
                () -> assertEquals(200L, userRole.getRoleId()),
                () -> assertEquals(now, userRole.getCreatedAt())
            );
        }

        @Test
        @DisplayName("Builder模式测试")
        void testBuilderPattern() {
            // When
            UserRole userRole = UserRole.builder()
                    .id(1L)
                    .userId(100L)
                    .roleId(200L)
                    .build();

            // Then
            assertAll(
                () -> assertEquals(1L, userRole.getId()),
                () -> assertEquals(100L, userRole.getUserId()),
                () -> assertEquals(200L, userRole.getRoleId()),
                () -> assertNull(userRole.getCreatedAt())
            );
        }

        @Test
        @DisplayName("便利构造函数测试")
        void testConvenienceConstructor() {
            // When
            UserRole userRole = new UserRole(100L, 200L);

            // Then
            assertAll(
                () -> assertNull(userRole.getId()),
                () -> assertEquals(100L, userRole.getUserId()),
                () -> assertEquals(200L, userRole.getRoleId()),
                () -> assertNotNull(userRole.getCreatedAt()) // 现在构造函数会自动设置createdAt
            );
        }
    }

    @Nested
    @DisplayName("数据验证测试")
    class DataValidationTests {

        @Test
        @DisplayName("必填字段测试")
        void testRequiredFields() {
            // Given & When
            UserRole userRole = UserRole.builder()
                    .userId(100L)
                    .roleId(200L)
                    .build();

            // Then
            assertAll(
                () -> assertNotNull(userRole.getUserId()),
                () -> assertNotNull(userRole.getRoleId()),
                () -> assertTrue(userRole.getUserId() > 0),
                () -> assertTrue(userRole.getRoleId() > 0)
            );
        }

        @Test
        @DisplayName("ID类型验证测试")
        void testIdTypes() {
            // Given
            Long userId = Long.MAX_VALUE;
            Long roleId = Long.MIN_VALUE;

            // When
            UserRole userRole = new UserRole(userId, roleId);

            // Then
            assertAll(
                () -> assertEquals(userId, userRole.getUserId()),
                () -> assertEquals(roleId, userRole.getRoleId())
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
            UserRole userRole = UserRole.builder()
                    .id(1L)
                    .userId(100L)
                    .roleId(200L)
                    .build();

            // When & Then
            assertEquals(userRole, userRole);
            assertEquals(userRole.hashCode(), userRole.hashCode());
        }

        @Test
        @DisplayName("相同内容equals测试")
        void testEqualsWithSameContent() {
            // Given
            UserRole userRole1 = UserRole.builder()
                    .id(1L)
                    .userId(100L)
                    .roleId(200L)
                    .build();

            UserRole userRole2 = UserRole.builder()
                    .id(1L)
                    .userId(100L)
                    .roleId(200L)
                    .build();

            // When & Then
            assertEquals(userRole1, userRole2);
            assertEquals(userRole1.hashCode(), userRole2.hashCode());
        }

        @Test
        @DisplayName("不同ID的equals测试")
        void testEqualsWithDifferentId() {
            // Given
            UserRole userRole1 = UserRole.builder().id(1L).userId(100L).roleId(200L).build();
            UserRole userRole2 = UserRole.builder().id(2L).userId(100L).roleId(200L).build();

            // When & Then
            assertNotEquals(userRole1, userRole2);
        }

        @Test
        @DisplayName("不同userId的equals测试")
        void testEqualsWithDifferentUserId() {
            // Given
            UserRole userRole1 = UserRole.builder().id(1L).userId(100L).roleId(200L).build();
            UserRole userRole2 = UserRole.builder().id(1L).userId(101L).roleId(200L).build();

            // When & Then
            assertNotEquals(userRole1, userRole2);
        }

        @Test
        @DisplayName("不同roleId的equals测试")
        void testEqualsWithDifferentRoleId() {
            // Given
            UserRole userRole1 = UserRole.builder().id(1L).userId(100L).roleId(200L).build();
            UserRole userRole2 = UserRole.builder().id(1L).userId(100L).roleId(201L).build();

            // When & Then
            assertNotEquals(userRole1, userRole2);
        }

        @Test
        @DisplayName("null对象equals测试")
        void testEqualsWithNull() {
            // Given
            UserRole userRole = UserRole.builder().id(1L).build();

            // When & Then
            assertNotEquals(userRole, null);
        }
    }

    @Nested
    @DisplayName("toString测试")
    class ToStringTests {

        @Test
        @DisplayName("toString基本功能测试")
        void testToString() {
            // Given
            UserRole userRole = UserRole.builder()
                    .id(1L)
                    .userId(100L)
                    .roleId(200L)
                    .build();

            // When
            String toString = userRole.toString();

            // Then
            assertAll(
                () -> assertNotNull(toString),
                () -> assertTrue(toString.contains("1")),
                () -> assertTrue(toString.contains("100")),
                () -> assertTrue(toString.contains("200"))
            );
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("零值ID测试")
        void testWithZeroIds() {
            // When
            UserRole userRole = new UserRole(0L, 0L);

            // Then
            assertAll(
                () -> assertEquals(0L, userRole.getUserId()),
                () -> assertEquals(0L, userRole.getRoleId())
            );
        }

        @Test
        @DisplayName("负值ID测试")
        void testWithNegativeIds() {
            // When
            UserRole userRole = new UserRole(-1L, -2L);

            // Then
            assertAll(
                () -> assertEquals(-1L, userRole.getUserId()),
                () -> assertEquals(-2L, userRole.getRoleId())
            );
        }

        @Test
        @DisplayName("最大值ID测试")
        void testWithMaxValueIds() {
            // When
            UserRole userRole = new UserRole(Long.MAX_VALUE, Long.MAX_VALUE);

            // Then
            assertAll(
                () -> assertEquals(Long.MAX_VALUE, userRole.getUserId()),
                () -> assertEquals(Long.MAX_VALUE, userRole.getRoleId())
            );
        }
    }
}
