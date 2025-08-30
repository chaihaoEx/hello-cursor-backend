package com.kinkle.helloquick.auth.service;

import com.kinkle.helloquick.auth.entity.Role;
import com.kinkle.helloquick.auth.entity.UserRole;
import com.kinkle.helloquick.auth.repository.RoleRepository;
import com.kinkle.helloquick.auth.repository.UserRoleRepository;
import com.kinkle.helloquick.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserRoleService 单元测试
 * <p>
 * 测试用户角色关联服务的各项功能，包括角色分配、移除、查询等操作。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户角色服务测试")
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    private Role adminRole;
    private Role userRole;
    private UserRole userRoleAssociation;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        adminRole = Role.builder()
                .id(1L)
                .roleCode("ADMIN")
                .roleName("管理员")
                .status(1)
                .build();

        userRole = Role.builder()
                .id(2L)
                .roleCode("USER")
                .roleName("普通用户")
                .status(1)
                .build();

        userRoleAssociation = UserRole.builder()
                .id(1L)
                .userId(100L)
                .roleId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("角色分配测试")
    class AssignRoleTests {

        @Test
        @DisplayName("成功为用户分配角色")
        void assignRole_Success() {
            // Given
            when(userRoleRepository.existsByUserIdAndRoleId(100L, 1L)).thenReturn(false);

            // When
            userRoleService.assignRole(100L, 1L);

            // Then
            ArgumentCaptor<UserRole> captor = ArgumentCaptor.forClass(UserRole.class);
            verify(userRoleRepository).save(captor.capture());
            UserRole savedUserRole = captor.getValue();
            assertThat(savedUserRole.getUserId()).isEqualTo(100L);
            assertThat(savedUserRole.getRoleId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("为用户分配已存在的角色时跳过")
        void assignRole_AlreadyExists() {
            // Given
            when(userRoleRepository.existsByUserIdAndRoleId(100L, 1L)).thenReturn(true);

            // When
            userRoleService.assignRole(100L, 1L);

            // Then
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }
    }

    @Nested
    @DisplayName("角色移除测试")
    class RemoveRoleTests {

        @Test
        @DisplayName("成功移除用户角色")
        void removeRole_Success() {
            // When
            userRoleService.removeRole(100L, 1L);

            // Then
            verify(userRoleRepository).deleteByUserIdAndRoleId(100L, 1L);
        }
    }

    @Nested
    @DisplayName("获取用户角色测试")
    class GetUserRolesTests {

        @Test
        @DisplayName("成功获取用户的角色列表")
        void getUserRoles_Success() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId(100L)).thenReturn(Arrays.asList(1L, 2L));
            when(roleRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(adminRole, userRole));

            // When
            List<Role> userRoles = userRoleService.getUserRoles(100L);

            // Then
            assertThat(userRoles).hasSize(2);
            assertThat(userRoles).contains(adminRole, userRole);
            verify(userRoleRepository).findRoleIdsByUserId(100L);
            verify(roleRepository).findAllById(Arrays.asList(1L, 2L));
        }

        @Test
        @DisplayName("获取用户角色时角色ID为空列表")
        void getUserRoles_EmptyRoleIds() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId(100L)).thenReturn(Arrays.asList());

            // When
            List<Role> userRoles = userRoleService.getUserRoles(100L);

            // Then
            assertThat(userRoles).isEmpty();
            verify(roleRepository).findAllById(Arrays.asList());
        }
    }

    @Nested
    @DisplayName("获取用户角色编码测试")
    class GetUserRoleCodesTests {

        @Test
        @DisplayName("成功获取用户的角色编码集合")
        void getUserRoleCodes_Success() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId(100L)).thenReturn(Arrays.asList(1L, 2L));
            when(roleRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(adminRole, userRole));

            // When
            Set<String> roleCodes = userRoleService.getUserRoleCodes(100L);

            // Then
            assertThat(roleCodes).hasSize(2);
            assertThat(roleCodes).contains("ADMIN", "USER");
        }

        @Test
        @DisplayName("获取用户角色编码时没有角色")
        void getUserRoleCodes_NoRoles() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId(100L)).thenReturn(Arrays.asList());
            when(roleRepository.findAllById(Arrays.asList())).thenReturn(Arrays.asList());

            // When
            Set<String> roleCodes = userRoleService.getUserRoleCodes(100L);

            // Then
            assertThat(roleCodes).isEmpty();
        }
    }

    @Nested
    @DisplayName("检查用户角色测试")
    class HasRoleTests {

        @Test
        @DisplayName("用户拥有指定角色")
        void hasRole_UserHasRole() {
            // Given
            when(roleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(adminRole));
            when(userRoleRepository.existsByUserIdAndRoleId(100L, 1L)).thenReturn(true);

            // When
            boolean hasRole = userRoleService.hasRole(100L, "ADMIN");

            // Then
            assertThat(hasRole).isTrue();
            verify(roleRepository).findByRoleCode("ADMIN");
            verify(userRoleRepository).existsByUserIdAndRoleId(100L, 1L);
        }

        @Test
        @DisplayName("用户没有指定角色")
        void hasRole_UserDoesNotHaveRole() {
            // Given
            when(roleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(adminRole));
            when(userRoleRepository.existsByUserIdAndRoleId(100L, 1L)).thenReturn(false);

            // When
            boolean hasRole = userRoleService.hasRole(100L, "ADMIN");

            // Then
            assertThat(hasRole).isFalse();
        }

        @Test
        @DisplayName("角色不存在时抛出异常")
        void hasRole_RoleNotFound() {
            // Given
            when(roleRepository.findByRoleCode("NON_EXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userRoleService.hasRole(100L, "NON_EXISTENT"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("角色不存在");

            verify(userRoleRepository, never()).existsByUserIdAndRoleId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class BoundaryTests {

        @Test
        @DisplayName("测试null参数处理")
        void handleNullParameters() {
            // Given - 模拟可能的null情况
            when(userRoleRepository.findRoleIdsByUserId(null)).thenReturn(Arrays.asList());

            // When & Then
            List<Role> userRoles = userRoleService.getUserRoles(null);
            assertThat(userRoles).isEmpty();
        }

        @Test
        @DisplayName("测试空字符串角色编码")
        void handleEmptyRoleCode() {
            // Given
            when(roleRepository.findByRoleCode("")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userRoleService.hasRole(100L, ""))
                    .isInstanceOf(BusinessException.class);
        }
    }
}
