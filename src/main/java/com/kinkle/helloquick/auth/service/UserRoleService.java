package com.kinkle.helloquick.auth.service;

import com.kinkle.helloquick.auth.entity.Role;
import com.kinkle.helloquick.auth.entity.UserRole;
import com.kinkle.helloquick.auth.repository.RoleRepository;
import com.kinkle.helloquick.auth.repository.UserRoleRepository;
import com.kinkle.helloquick.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色关联服务
 * <p>
 * 处理用户与角色的关联关系。
 * 遵循spring-architect.mdc原则：通过Service处理跨模块调用。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    /**
     * 为用户分配角色
     */
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        if (!userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            UserRole userRole = new UserRole(userId, roleId);
            userRoleRepository.save(userRole);
            log.info("用户角色分配成功: userId={}, roleId={}", userId, roleId);
        }
    }

    /**
     * 移除用户角色
     */
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        log.info("用户角色移除成功: userId={}, roleId={}", userId, roleId);
    }

    /**
     * 获取用户的角色列表
     */
    public List<Role> getUserRoles(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleRepository.findAllById(roleIds);
    }

    /**
     * 获取用户的角色编码
     */
    public Set<String> getUserRoleCodes(Long userId) {
        return getUserRoles(userId).stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean hasRole(Long userId, String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> BusinessException.dataNotFound("角色"));
        return userRoleRepository.existsByUserIdAndRoleId(userId, role.getId());
    }
}
