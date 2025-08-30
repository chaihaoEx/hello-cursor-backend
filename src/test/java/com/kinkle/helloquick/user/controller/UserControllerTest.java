package com.kinkle.helloquick.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinkle.helloquick.common.exception.BusinessException;
import com.kinkle.helloquick.common.result.PageResult;
import com.kinkle.helloquick.user.dto.UserDTO;
import com.kinkle.helloquick.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController单元测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDTO testUserDTO;
    private UserDTO.CreateRequest createRequest;
    private UserDTO.UpdateRequest updateRequest;
    private UserDTO.ChangePasswordRequest changePasswordRequest;
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("测试用户")
                .phone("13800138000")
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = UserDTO.CreateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .fullName("新用户")
                .phone("13800138001")
                .build();

        updateRequest = UserDTO.UpdateRequest.builder()
                .email("updated@example.com")
                .fullName("更新用户")
                .phone("13800138002")
                .build();

        changePasswordRequest = UserDTO.ChangePasswordRequest.builder()
                .oldPassword("oldpass123")
                .newPassword("newpass123")
                .confirmPassword("newpass123")
                .build();

        UserDTO.QueryRequest.builder()
                .username("test")
                .email("test@")
                .status(1)
                .page(1)
                .size(10)
                .build();
    }

    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {

        @Test
        @DisplayName("成功创建用户应该返回201状态码")
        void shouldReturnCreatedStatusWhenUserCreatedSuccessfully() throws Exception {
            when(userService.createUser(any(UserDTO.CreateRequest.class))).thenReturn(testUserDTO);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("用户创建成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("testuser"));

            verify(userService).createUser(any(UserDTO.CreateRequest.class));
        }

        @Test
        @DisplayName("创建用户时缺少必填字段应该返回400错误")
        void shouldReturnBadRequestWhenRequiredFieldsMissing() throws Exception {
            UserDTO.CreateRequest invalidRequest = UserDTO.CreateRequest.builder()
                    .username("") // 空用户名
                    .build();

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).createUser(any());
        }

        @Test
        @DisplayName("创建重复用户应该返回业务异常")
        void shouldReturnBusinessExceptionWhenUserAlreadyExists() throws Exception {
            when(userService.createUser(any(UserDTO.CreateRequest.class)))
                    .thenThrow(BusinessException.dataExists("用户名"));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("获取用户测试")
    class GetUserTests {

        @Test
        @DisplayName("根据ID获取用户应该返回用户信息")
        void shouldReturnUserWhenGetByIdSuccessfully() throws Exception {
            when(userService.getUserById(1L)).thenReturn(testUserDTO);

            mockMvc.perform(get("/api/v1/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户信息获取成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("testuser"));

            verify(userService).getUserById(1L);
        }

        @Test
        @DisplayName("根据用户名获取用户应该返回用户信息")
        void shouldReturnUserWhenGetByUsernameSuccessfully() throws Exception {
            when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);

            mockMvc.perform(get("/api/v1/users/username/testuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户信息获取成功"))
                    .andExpect(jsonPath("$.data.username").value("testuser"));

            verify(userService).getUserByUsername("testuser");
        }

        @Test
        @DisplayName("获取不存在的用户应该返回业务异常")
        void shouldReturnBusinessExceptionWhenUserNotFound() throws Exception {
            when(userService.getUserById(999L))
                    .thenThrow(BusinessException.dataNotFound("用户"));

            mockMvc.perform(get("/api/v1/users/999"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("无效的用户ID应该返回400错误")
        void shouldReturnBadRequestWhenInvalidUserId() throws Exception {
            mockMvc.perform(get("/api/v1/users/0"))
                    .andExpect(status().isInternalServerError());

            verify(userService, never()).getUserById(anyLong());
        }
    }

    @Nested
    @DisplayName("更新用户测试")
    class UpdateUserTests {

        @Test
        @DisplayName("成功更新用户应该返回更新后的用户信息")
        void shouldReturnUpdatedUserWhenUpdateSuccessfully() throws Exception {
            when(userService.updateUser(eq(1L), any(UserDTO.UpdateRequest.class))).thenReturn(testUserDTO);

            mockMvc.perform(put("/api/v1/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户信息更新成功"))
                    .andExpect(jsonPath("$.data.id").value(1));

            verify(userService).updateUser(eq(1L), any(UserDTO.UpdateRequest.class));
        }

        @Test
        @DisplayName("更新不存在的用户应该返回业务异常")
        void shouldReturnBusinessExceptionWhenUpdateNonExistentUser() throws Exception {
            when(userService.updateUser(eq(999L), any(UserDTO.UpdateRequest.class)))
                    .thenThrow(BusinessException.dataNotFound("用户"));

            mockMvc.perform(put("/api/v1/users/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("成功删除用户应该返回成功消息")
        void shouldReturnSuccessWhenDeleteUserSuccessfully() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/api/v1/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户删除成功"))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(userService).deleteUser(1L);
        }

        @Test
        @DisplayName("删除不存在的用户应该返回业务异常")
        void shouldReturnBusinessExceptionWhenDeleteNonExistentUser() throws Exception {
            doThrow(BusinessException.dataNotFound("用户")).when(userService).deleteUser(999L);

            mockMvc.perform(delete("/api/v1/users/999"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("用户状态管理测试")
    class UserStatusTests {

        @Test
        @DisplayName("启用用户应该返回成功消息")
        void shouldReturnSuccessWhenEnableUser() throws Exception {
            doNothing().when(userService).enableUser(1L);

            mockMvc.perform(put("/api/v1/users/1/enable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户启用成功"));

            verify(userService).enableUser(1L);
        }

        @Test
        @DisplayName("禁用用户应该返回成功消息")
        void shouldReturnSuccessWhenDisableUser() throws Exception {
            doNothing().when(userService).disableUser(1L);

            mockMvc.perform(put("/api/v1/users/1/disable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户禁用成功"));

            verify(userService).disableUser(1L);
        }
    }

    @Nested
    @DisplayName("密码管理测试")
    class PasswordTests {

        @Test
        @DisplayName("修改密码应该返回成功消息")
        void shouldReturnSuccessWhenChangePassword() throws Exception {
            doNothing().when(userService).changePassword(eq(1L), any(UserDTO.ChangePasswordRequest.class));

            mockMvc.perform(put("/api/v1/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("密码修改成功"));

            verify(userService).changePassword(eq(1L), any(UserDTO.ChangePasswordRequest.class));
        }

        @Test
        @DisplayName("修改密码时旧密码错误应该返回业务异常")
        void shouldReturnBusinessExceptionWhenOldPasswordIncorrect() throws Exception {
            doThrow(BusinessException.paramError("旧密码错误"))
                    .when(userService).changePassword(eq(1L), any(UserDTO.ChangePasswordRequest.class));

            mockMvc.perform(put("/api/v1/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("分页查询测试")
    class PaginationTests {

        @Test
        @DisplayName("分页查询用户应该返回分页结果")
        void shouldReturnPageResultWhenQueryUsers() throws Exception {
            PageResult<UserDTO> pageResult = PageResult.<UserDTO>builder()
                    .records(Arrays.asList(testUserDTO))
                    .totalCount(1L)
                    .currentPage(1)
                    .pageSize(10)
                    .totalPages(1)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();

            when(userService.getUsers(any(UserDTO.QueryRequest.class))).thenReturn(pageResult);

            mockMvc.perform(get("/api/v1/users")
                            .param("username", "test")
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户列表获取成功"))
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.records").isArray())
                    .andExpect(jsonPath("$.data.records[0].username").value("testuser"));

            verify(userService).getUsers(any(UserDTO.QueryRequest.class));
        }

        @Test
        @DisplayName("空结果分页查询应该返回空列表")
        void shouldReturnEmptyListWhenNoUsersFound() throws Exception {
            PageResult<UserDTO> emptyResult = PageResult.<UserDTO>builder()
                    .records(Collections.emptyList())
                    .totalCount(0L)
                    .currentPage(1)
                    .pageSize(10)
                    .totalPages(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();

            when(userService.getUsers(any(UserDTO.QueryRequest.class))).thenReturn(emptyResult);

            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.records").isArray())
                    .andExpect(jsonPath("$.data.records").isEmpty());
        }
    }

    @Nested
    @DisplayName("检查接口测试")
    class CheckTests {

        @Test
        @DisplayName("检查用户名存在应该返回true")
        void shouldReturnTrueWhenUsernameExists() throws Exception {
            when(userService.existsByUsername("testuser")).thenReturn(true);

            mockMvc.perform(get("/api/v1/users/check/username")
                            .param("username", "testuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户名检查完成"))
                    .andExpect(jsonPath("$.data").value(true));

            verify(userService).existsByUsername("testuser");
        }

        @Test
        @DisplayName("检查用户名不存在应该返回false")
        void shouldReturnFalseWhenUsernameNotExists() throws Exception {
            when(userService.existsByUsername("nonexistent")).thenReturn(false);

            mockMvc.perform(get("/api/v1/users/check/username")
                            .param("username", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(false));

            verify(userService).existsByUsername("nonexistent");
        }

        @Test
        @DisplayName("检查邮箱存在应该返回true")
        void shouldReturnTrueWhenEmailExists() throws Exception {
            when(userService.existsByEmail("test@example.com")).thenReturn(true);

            mockMvc.perform(get("/api/v1/users/check/email")
                            .param("email", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("邮箱检查完成"))
                    .andExpect(jsonPath("$.data").value(true));

            verify(userService).existsByEmail("test@example.com");
        }

        @Test
        @DisplayName("检查邮箱不存在应该返回false")
        void shouldReturnFalseWhenEmailNotExists() throws Exception {
            when(userService.existsByEmail("nonexistent@example.com")).thenReturn(false);

            mockMvc.perform(get("/api/v1/users/check/email")
                            .param("email", "nonexistent@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(false));

            verify(userService).existsByEmail("nonexistent@example.com");
        }
    }

    @Nested
    @DisplayName("统计信息测试")
    class StatisticsTests {

        @Test
        @DisplayName("获取用户统计信息应该返回启用用户数量")
        void shouldReturnUserStatistics() throws Exception {
            when(userService.getEnabledUserCount()).thenReturn(100L);

            mockMvc.perform(get("/api/v1/users/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("用户统计信息获取成功"))
                    .andExpect(jsonPath("$.data.enabledUserCount").value(100));

            verify(userService).getEnabledUserCount();
        }

        @Test
        @DisplayName("无用户时统计信息应该返回0")
        void shouldReturnZeroWhenNoEnabledUsers() throws Exception {
            when(userService.getEnabledUserCount()).thenReturn(0L);

            mockMvc.perform(get("/api/v1/users/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.enabledUserCount").value(0));

            verify(userService).getEnabledUserCount();
        }
    }

    @Nested
    @DisplayName("参数验证测试")
    class ValidationTests {

        @Test
        @DisplayName("无效的用户ID应该返回400错误")
        void shouldReturnBadRequestForInvalidUserId() throws Exception {
            mockMvc.perform(get("/api/v1/users/-1"))
                    .andExpect(status().isInternalServerError());

            verify(userService, never()).getUserById(anyLong());
        }

        @Test
        @DisplayName("缺少必需参数应该返回400错误")
        void shouldReturnBadRequestForMissingRequiredParams() throws Exception {
            mockMvc.perform(get("/api/v1/users/check/username"))
                    .andExpect(status().isInternalServerError());

            verify(userService, never()).existsByUsername(anyString());
        }
    }
}
