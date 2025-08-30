package com.kinkle.helloquick.auth.repository;

import com.kinkle.helloquick.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问层
 * <p>
 * 遵循spring-architect.mdc原则：简单Repository，专注关联查询。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
    
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
}