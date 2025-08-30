package com.kinkle.helloquick.auth.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Role实体类单元测试
 * <p>
 * 测试覆盖率目标：95%+
 * 遵循pr-review.mdc的测试质量标准。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@DisplayName("Role 角色实体类测试")
class RoleTest {

    @Nested
    @DisplayName("构造函数和Builder测试")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("无参构造函数测试")
        void testNoArgsConstructor() {
            // When
            Role role = new Role();

            // Then
            assertAll(
                () -> assertNull(role.getId()),
                () -> assertNull(role.getRoleName()),
                () -> assertNull(role.getRoleCode()),
                () -> assertNull(role.getDescription()),
                () -> assertEquals(1, role.getStatus()), // 默认值为1
                () -> assertNull(role.getCreatedAt()),
                () -> assertNull(role.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("全参构造函数测试")
        void testAllArgsConstructor() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            Role role = new Role(1L, "管理员", "ADMIN", "系统管理员", 1, now, now);

            // Then
            assertAll(
                () -> assertEquals(1L, role.getId()),
                () -> assertEquals("管理员", role.getRoleName()),
                () -> assertEquals("ADMIN", role.getRoleCode()),
                () -> assertEquals("系统管理员", role.getDescription()),
                () -> assertEquals(1, role.getStatus()),
                () -> assertEquals(now, role.getCreatedAt()),
                () -> assertEquals(now, role.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("Builder模式测试")
        void testBuilderPattern() {
            // When
            Role role = Role.builder()
                    .id(1L)
                    .roleName("用户")
                    .roleCode("USER")
                    .description("普通用户")
                    .build();

            // Then
            assertAll(
                () -> assertEquals(1L, role.getId()),
                () -> assertEquals("用户", role.getRoleName()),
                () -> assertEquals("USER", role.getRoleCode()),
                () -> assertEquals("普通用户", role.getDescription()),
                () -> assertEquals(1, role.getStatus()) // 默认值
            );
        }

        @Test
        @DisplayName("Builder默认值测试")
        void testBuilderDefaults() {
            // When
            Role role = Role.builder()
                    .roleName("测试角色")
                    .roleCode("TEST")
                    .build();

            // Then
            assertEquals(1, role.getStatus()); // 默认状态为1（启用）
        }
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("isEnabled方法测试")
        void testIsEnabled() {
            Role role = Role.builder().build();

            // 启用状态测试
            role.setStatus(1);
            assertTrue(role.isEnabled());

            // 禁用状态测试
            role.setStatus(0);
            assertFalse(role.isEnabled());

            // null状态测试
            role.setStatus(null);
            assertFalse(role.isEnabled());

            // 其他数值测试
            role.setStatus(2);
            assertFalse(role.isEnabled());
        }

        @Test
        @DisplayName("状态设置测试")
        void testStatusSetting() {
            // Given
            Role role = Role.builder().build();

            // When & Then
            role.setStatus(1);
            assertTrue(role.isEnabled());

            role.setStatus(0);
            assertFalse(role.isEnabled());
        }
    }



    @Nested
    @DisplayName("数据验证测试")
    class DataValidationTests {

        @Test
        @DisplayName("字段长度限制测试")
        void testFieldLengths() {
            // Given
            Role role = Role.builder()
                    .roleName("a".repeat(50))     // 最大长度
                    .roleCode("a".repeat(50))     // 最大长度
                    .description("a".repeat(1000)) // 长描述
                    .build();

            // Then
            assertAll(
                () -> assertEquals(50, role.getRoleName().length()),
                () -> assertEquals(50, role.getRoleCode().length()),
                () -> assertEquals(1000, role.getDescription().length())
            );
        }

        @Test
        @DisplayName("可空字段测试")
        void testNullableFields() {
            // Given & When
            Role role = Role.builder()
                    .roleName("测试角色")
                    .roleCode("TEST")
                    .description(null)  // 可空
                    .build();

            // Then
            assertAll(
                () -> assertNotNull(role.getRoleName()),
                () -> assertNotNull(role.getRoleCode()),
                () -> assertNull(role.getDescription())
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
            Role role = Role.builder().id(1L).roleCode("TEST").build();

            // When & Then
            assertEquals(role, role);
            assertEquals(role.hashCode(), role.hashCode());
        }

        @Test
        @DisplayName("相同内容equals测试")
        void testEqualsWithSameContent() {
            // Given
            Role role1 = Role.builder().id(1L).roleCode("TEST").roleName("测试").build();
            Role role2 = Role.builder().id(1L).roleCode("TEST").roleName("测试").build();

            // When & Then
            assertEquals(role1, role2);
            assertEquals(role1.hashCode(), role2.hashCode());
        }

        @Test
        @DisplayName("不同ID的equals测试")
        void testEqualsWithDifferentId() {
            // Given
            Role role1 = Role.builder().id(1L).roleCode("TEST").build();
            Role role2 = Role.builder().id(2L).roleCode("TEST").build();

            // When & Then
            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("不同roleCode的equals测试")
        void testEqualsWithDifferentRoleCode() {
            // Given
            Role role1 = Role.builder().id(1L).roleCode("TEST1").build();
            Role role2 = Role.builder().id(1L).roleCode("TEST2").build();

            // When & Then
            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("null对象equals测试")
        void testEqualsWithNull() {
            // Given
            Role role = Role.builder().id(1L).build();

            // When & Then
            assertNotEquals(role, null);
        }
    }
}
