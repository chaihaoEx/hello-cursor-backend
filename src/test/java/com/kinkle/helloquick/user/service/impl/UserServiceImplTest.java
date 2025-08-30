package com.kinkle.helloquick.user.service.impl;

import com.kinkle.helloquick.common.exception.BusinessException;
import com.kinkle.helloquick.common.result.PageResult;
import com.kinkle.helloquick.user.dto.UserDTO;
import com.kinkle.helloquick.user.entity.User;
import com.kinkle.helloquick.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试类
 *
 * 测试所有业务逻辑方法，验证正常流程和异常处理
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务实现测试")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO.CreateRequest createRequest;
    private UserDTO.UpdateRequest updateRequest;
    private UserDTO.ChangePasswordRequest changePasswordRequest;
    private UserDTO.QueryRequest queryRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // 初始化测试用户
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .fullName("Test User")
                .phone("13800138000")
                .status(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 初始化创建请求
        createRequest = UserDTO.CreateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .fullName("New User")
                .phone("13800138001")
                .build();

        // 初始化更新请求
        updateRequest = UserDTO.UpdateRequest.builder()
                .email("updated@example.com")
                .fullName("Updated User")
                .phone("13800138002")
                .build();

        // 初始化密码修改请求
        changePasswordRequest = UserDTO.ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        // 初始化查询请求
        queryRequest = UserDTO.QueryRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .status(1)
                .page(1)
                .size(10)
                .build();
    }

    @Nested
    @DisplayName("用户创建测试")
    class CreateUserTests {

        @Test
        @DisplayName("成功创建用户")
        void createUser_Success() {
            // Given
            User newUser = User.builder()
                    .id(2L)
                    .username("newuser")
                    .email("new@example.com")
                    .password("encodedPassword")
                    .fullName("New User")
                    .phone("13800138001")
                    .status(1)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            // When
            UserDTO result = userService.createUser(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("new@example.com", result.getEmail());
            assertEquals("New User", result.getFullName());
            assertEquals(1, result.getStatus());

            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("new@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("创建用户时用户名已存在")
        void createUser_UsernameExists() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.createUser(createRequest));

            assertEquals("用户名已存在", exception.getMessage());
            verify(userRepository).existsByUsername("newuser");
            verify(userRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("创建用户时邮箱已存在")
        void createUser_EmailExists() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.createUser(createRequest));

            assertEquals("邮箱已存在", exception.getMessage());
            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("new@example.com");
        }
    }

    @Nested
    @DisplayName("用户查询测试")
    class GetUserTests {

        @Test
        @DisplayName("根据ID成功获取用户")
        void getUserById_Success() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            UserDTO result = userService.getUserById(1L);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("testuser", result.getUsername());
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("根据ID获取不存在的用户")
        void getUserById_NotFound() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserById(999L));

            assertEquals("用户不存在", exception.getMessage());
        }

        @Test
        @DisplayName("根据用户名成功获取用户")
        void getUserByUsername_Success() {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When
            UserDTO result = userService.getUserByUsername("testuser");

            // Then
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("根据用户名获取不存在的用户")
        void getUserByUsername_NotFound() {
            // Given
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserByUsername("nonexistent"));

            assertEquals("用户不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("用户更新测试")
    class UpdateUserTests {

        @Test
        @DisplayName("成功更新用户信息")
        void updateUser_Success() {
            // Given
            User updatedUser = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("updated@example.com")
                    .password("encodedPassword")
                    .fullName("Updated User")
                    .phone("13800138002")
                    .status(1)
                    .createdAt(now)
                    .updatedAt(now.plusMinutes(1))
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            // When
            UserDTO result = userService.updateUser(1L, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals("updated@example.com", result.getEmail());
            assertEquals("Updated User", result.getFullName());
            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmail("updated@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("更新不存在的用户")
        void updateUser_NotFound() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(999L, updateRequest));

            assertEquals("用户不存在", exception.getMessage());
        }

        @Test
        @DisplayName("更新用户时邮箱被其他用户占用")
        void updateUser_EmailExists() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(1L, updateRequest));

            assertEquals("邮箱已存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("用户删除测试")
    class DeleteUserTests {

        @Test
        @DisplayName("成功删除用户")
        void deleteUser_Success() {
            // Given
            when(userRepository.existsById(1L)).thenReturn(true);

            // When
            userService.deleteUser(1L);

            // Then
            verify(userRepository).existsById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的用户")
        void deleteUser_NotFound() {
            // Given
            when(userRepository.existsById(999L)).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.deleteUser(999L));

            assertEquals("用户不存在", exception.getMessage());
            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("用户状态管理测试")
    class UserStatusTests {

        @Test
        @DisplayName("成功启用用户")
        void enableUser_Success() {
            // Given
            User disabledUser = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("encodedPassword")
                    .status(0)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(disabledUser));
            when(userRepository.save(any(User.class))).thenReturn(disabledUser);

            // When
            userService.enableUser(1L);

            // Then
            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("成功禁用用户")
        void disableUser_Success() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.disableUser(1L);

            // Then
            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("启用不存在的用户")
        void enableUser_NotFound() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.enableUser(999L));

            assertEquals("用户不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("密码修改测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("成功修改密码")
        void changePassword_Success() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.changePassword(1L, changePasswordRequest);

            // Then
            verify(userRepository).findById(1L);
            verify(passwordEncoder).matches("oldPassword", "encodedPassword");
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("修改密码时原密码不正确")
        void changePassword_WrongOldPassword() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.changePassword(1L, changePasswordRequest));

            assertEquals("原密码不正确", exception.getMessage());
        }

        @Test
        @DisplayName("修改密码时新密码和确认密码不一致")
        void changePassword_PasswordMismatch() {
            // Given
            UserDTO.ChangePasswordRequest invalidRequest = UserDTO.ChangePasswordRequest.builder()
                    .oldPassword("oldPassword")
                    .newPassword("newPassword123")
                    .confirmPassword("differentPassword")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.changePassword(1L, invalidRequest));

            assertEquals("新密码和确认密码不一致", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("用户分页查询测试")
    class GetUsersTests {

        @Test
        @DisplayName("成功分页查询用户")
        void getUsers_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

            when(userRepository.findByConditions(anyString(), anyString(), any(), any(Pageable.class)))
                    .thenReturn(userPage);

            // When
            PageResult<UserDTO> result = userService.getUsers(queryRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalCount());
            assertEquals(1, result.getRecords().size());
            assertEquals("testuser", result.getRecords().get(0).getUsername());

            verify(userRepository).findByConditions(
                    eq("testuser"), eq("test@example.com"), eq(1), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("用户存在性检查测试")
    class ExistsTests {

        @Test
        @DisplayName("检查用户名是否存在")
        void existsByUsername() {
            // Given
            when(userRepository.existsByUsername("testuser")).thenReturn(true);
            when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

            // When & Then
            assertTrue(userService.existsByUsername("testuser"));
            assertFalse(userService.existsByUsername("nonexistent"));

            verify(userRepository, times(2)).existsByUsername(anyString());
        }

        @Test
        @DisplayName("检查邮箱是否存在")
        void existsByEmail() {
            // Given
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

            // When & Then
            assertTrue(userService.existsByEmail("test@example.com"));
            assertFalse(userService.existsByEmail("new@example.com"));

            verify(userRepository, times(2)).existsByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("用户计数测试")
    class CountTests {

        @Test
        @DisplayName("获取启用用户数量")
        void getEnabledUserCount() {
            // Given
            when(userRepository.countEnabledUsers()).thenReturn(5L);

            // When
            long count = userService.getEnabledUserCount();

            // Then
            assertEquals(5L, count);
            verify(userRepository).countEnabledUsers();
        }
    }

    @Nested
    @DisplayName("数据转换测试")
    class ConversionTests {

        @Test
        @DisplayName("实体转换为DTO")
        void convertToDTO() {
            // When
            UserDTO result = userService.convertToDTO(testUser);

            // Then
            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getUsername(), result.getUsername());
            assertEquals(testUser.getEmail(), result.getEmail());
            assertEquals(testUser.getFullName(), result.getFullName());
            assertEquals(testUser.getPhone(), result.getPhone());
            assertEquals(testUser.getStatus(), result.getStatus());
        }

        @Test
        @DisplayName("DTO转换为实体")
        void convertToEntity() {
            // Given
            UserDTO userDTO = UserDTO.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .fullName("Test User")
                    .phone("13800138000")
                    .status(1)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // When
            User result = userService.convertToEntity(userDTO);

            // Then
            assertNotNull(result);
            assertEquals(userDTO.getId(), result.getId());
            assertEquals(userDTO.getUsername(), result.getUsername());
            assertEquals(userDTO.getEmail(), result.getEmail());
            assertEquals(userDTO.getFullName(), result.getFullName());
            assertEquals(userDTO.getPhone(), result.getPhone());
            assertEquals(userDTO.getStatus(), result.getStatus());
        }

        @Test
        @DisplayName("转换null值")
        void convertNullValues() {
            // When & Then
            assertNull(userService.convertToDTO(null));
            assertNull(userService.convertToEntity(null));
        }
    }
}
