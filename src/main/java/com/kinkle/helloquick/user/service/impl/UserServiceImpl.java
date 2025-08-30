package com.kinkle.helloquick.user.service.impl;

import com.kinkle.helloquick.common.exception.BusinessException;
import com.kinkle.helloquick.common.result.PageResult;
import com.kinkle.helloquick.user.dto.UserDTO;
import com.kinkle.helloquick.user.entity.User;
import com.kinkle.helloquick.user.repository.UserRepository;
import com.kinkle.helloquick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * <p>
 * 实现用户相关的业务逻辑。
 * 遵循spring-architect.mdc的服务层实现规范。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO.CreateRequest createRequest) {
        log.info("创建用户: {}", createRequest.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(createRequest.getUsername())) {
            throw BusinessException.dataExists("用户名");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw BusinessException.dataExists("邮箱");
        }

        // 创建用户实体
        User user = User.builder()
                .username(createRequest.getUsername())
                .email(createRequest.getEmail())
                .password(passwordEncoder.encode(createRequest.getPassword()))
                .fullName(createRequest.getFullName())
                .phone(createRequest.getPhone())
                .status(1)
                .build();

        User savedUser = userRepository.save(user);
        log.info("用户创建成功: ID={}, Username={}", savedUser.getId(), savedUser.getUsername());

        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.debug("根据ID获取用户: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        log.debug("根据用户名获取用户: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO.UpdateRequest updateRequest) {
        log.info("更新用户信息: ID={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        // 检查邮箱是否被其他用户使用
        if (!user.getEmail().equals(updateRequest.getEmail()) &&
            userRepository.existsByEmail(updateRequest.getEmail())) {
            throw BusinessException.dataExists("邮箱");
        }

        // 更新用户信息
        user.setEmail(updateRequest.getEmail());
        user.setFullName(updateRequest.getFullName());
        user.setPhone(updateRequest.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: ID={}, Username={}", updatedUser.getId(), updatedUser.getUsername());

        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: ID={}", id);

        if (!userRepository.existsById(id)) {
            throw BusinessException.dataNotFound("用户");
        }

        userRepository.deleteById(id);
        log.info("用户删除成功: ID={}", id);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        log.info("启用用户: ID={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        user.enable();
        userRepository.save(user);
        log.info("用户启用成功: ID={}, Username={}", user.getId(), user.getUsername());
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        log.info("禁用用户: ID={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        user.disable();
        userRepository.save(user);
        log.info("用户禁用成功: ID={}, Username={}", user.getId(), user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(Long id, UserDTO.ChangePasswordRequest changePasswordRequest) {
        log.info("修改用户密码: ID={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.dataNotFound("用户"));

        // 验证原密码
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw BusinessException.paramError("原密码不正确");
        }

        // 验证新密码和确认密码是否一致
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw BusinessException.paramError("新密码和确认密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("用户密码修改成功: ID={}, Username={}", user.getId(), user.getUsername());
    }

    @Override
    public PageResult<UserDTO> getUsers(UserDTO.QueryRequest queryRequest) {
        log.debug("分页查询用户: {}", queryRequest);

        // 创建分页参数
        Pageable pageable = PageRequest.of(
                queryRequest.getPage() - 1,
                queryRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // 执行查询
        Page<User> userPage = userRepository.findByConditions(
                queryRequest.getUsername(),
                queryRequest.getEmail(),
                queryRequest.getStatus(),
                pageable
        );

        // 转换为DTO
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(
                userDTOs,
                queryRequest.getPage(),
                queryRequest.getSize(),
                userPage.getTotalElements()
        );
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public long getEnabledUserCount() {
        return userRepository.countEnabledUsers();
    }

    @Override
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .phone(userDTO.getPhone())
                .status(userDTO.getStatus())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .build();
    }
}
