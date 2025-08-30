package com.kinkle.helloquick.user.repository;

import com.kinkle.helloquick.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * <p>
 * 提供用户相关的数据库操作方法。
 * 遵循spring-architect.mdc的Repository设计规范。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     *
     * @param username 用户名
     * @param email    邮箱
     * @return 用户信息
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态分页查询用户
     *
     * @param status   用户状态
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据用户名模糊查询
     *
     * @param username 用户名关键字
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * 根据全名模糊查询
     *
     * @param fullName 全名关键字
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    /**
     * 统计启用的用户数量
     *
     * @return 启用用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    long countEnabledUsers();

    /**
     * 根据多个条件查询用户
     *
     * @param username 用户名关键字
     * @param email    邮箱关键字
     * @param status   用户状态
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findByConditions(@Param("username") String username,
                                @Param("email") String email,
                                @Param("status") Integer status,
                                Pageable pageable);
}
