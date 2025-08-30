package com.kinkle.helloquick.auth.repository;

import com.kinkle.helloquick.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问层
 * <p>
 * 遵循spring-architect.mdc原则：简单Repository，专注数据访问。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);
    
    boolean existsByRoleCode(String roleCode);
    
    List<Role> findByStatus(Integer status);
}