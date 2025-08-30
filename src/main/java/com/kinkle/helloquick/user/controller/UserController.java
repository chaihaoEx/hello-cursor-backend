package com.kinkle.helloquick.user.controller;

import com.kinkle.helloquick.common.result.PageResult;
import com.kinkle.helloquick.common.result.Result;
import com.kinkle.helloquick.user.dto.UserDTO;
import com.kinkle.helloquick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * 用户控制器
 * <p>
 * 提供用户相关的REST API接口。
 * 遵循spring-architect.mdc的控制器设计规范。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     *
     * @param createRequest 创建用户请求
     * @return 用户信息
     */
    @PostMapping
    public ResponseEntity<Result<UserDTO>> createUser(@Valid @RequestBody UserDTO.CreateRequest createRequest) {
        log.info("创建用户请求: {}", createRequest.getUsername());

        UserDTO userDTO = userService.createUser(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(userDTO, "用户创建成功"));
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> getUserById(@PathVariable @Min(1) Long id) {
        log.info("获取用户详情请求: ID={}", id);

        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(Result.success(userDTO, "用户信息获取成功"));
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Result<UserDTO>> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户请求: {}", username);

        UserDTO userDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(Result.success(userDTO, "用户信息获取成功"));
    }

    /**
     * 更新用户信息
     *
     * @param id            用户ID
     * @param updateRequest 更新请求
     * @return 用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<UserDTO>> updateUser(@PathVariable @Min(1) Long id,
                                                      @Valid @RequestBody UserDTO.UpdateRequest updateRequest) {
        log.info("更新用户信息请求: ID={}", id);

        UserDTO userDTO = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(Result.success(userDTO, "用户信息更新成功"));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable @Min(1) Long id) {
        log.info("删除用户请求: ID={}", id);

        userService.deleteUser(id);
        return ResponseEntity.ok(Result.success(null, "用户删除成功"));
    }

    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Result<Void>> enableUser(@PathVariable @Min(1) Long id) {
        log.info("启用用户请求: ID={}", id);

        userService.enableUser(id);
        return ResponseEntity.ok(Result.success(null, "用户启用成功"));
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Result<Void>> disableUser(@PathVariable @Min(1) Long id) {
        log.info("禁用用户请求: ID={}", id);

        userService.disableUser(id);
        return ResponseEntity.ok(Result.success(null, "用户禁用成功"));
    }

    /**
     * 修改密码
     *
     * @param id                    用户ID
     * @param changePasswordRequest 修改密码请求
     * @return 操作结果
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Result<Void>> changePassword(@PathVariable @Min(1) Long id,
                                                       @Valid @RequestBody UserDTO.ChangePasswordRequest changePasswordRequest) {
        log.info("修改密码请求: ID={}", id);

        userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok(Result.success(null, "密码修改成功"));
    }

    /**
     * 分页查询用户
     *
     * @param queryRequest 查询请求
     * @return 用户分页数据
     */
    @GetMapping
    public ResponseEntity<Result<PageResult<UserDTO>>> getUsers(@Valid UserDTO.QueryRequest queryRequest) {
        log.info("分页查询用户请求: {}", queryRequest);

        PageResult<UserDTO> pageResult = userService.getUsers(queryRequest);
        return ResponseEntity.ok(Result.success(pageResult, "用户列表获取成功"));
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check/username")
    public ResponseEntity<Result<Boolean>> checkUsername(@RequestParam String username) {
        log.info("检查用户名是否存在: {}", username);

        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Result.success(exists, "用户名检查完成"));
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check/email")
    public ResponseEntity<Result<Boolean>> checkEmail(@RequestParam String email) {
        log.info("检查邮箱是否存在: {}", email);

        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Result.success(exists, "邮箱检查完成"));
    }

    /**
     * 获取用户统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Result<UserStatistics>> getUserStatistics() {
        log.info("获取用户统计信息请求");

        long enabledCount = userService.getEnabledUserCount();
        UserStatistics statistics = UserStatistics.builder()
                .enabledUserCount(enabledCount)
                .build();

        return ResponseEntity.ok(Result.success(statistics, "用户统计信息获取成功"));
    }

    /**
     * 用户统计信息DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStatistics {
        private long enabledUserCount;
    }
}
