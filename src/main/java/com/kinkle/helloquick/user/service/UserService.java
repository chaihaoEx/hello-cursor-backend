package com.kinkle.helloquick.user.service;

import com.kinkle.helloquick.common.result.PageResult;
import com.kinkle.helloquick.user.dto.UserDTO;
import com.kinkle.helloquick.user.entity.User;

/**
 * 用户服务接口
 * <p>
 * 定义用户相关的业务操作方法。
 * 遵循spring-architect.mdc的服务层接口设计规范。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param createRequest 创建用户请求
     * @return 用户信息
     */
    UserDTO createUser(UserDTO.CreateRequest createRequest);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long id);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDTO getUserByUsername(String username);

    /**
     * 更新用户信息
     *
     * @param id            用户ID
     * @param updateRequest 更新请求
     * @return 用户信息
     */
    UserDTO updateUser(Long id, UserDTO.UpdateRequest updateRequest);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 启用用户
     *
     * @param id 用户ID
     */
    void enableUser(Long id);

    /**
     * 禁用用户
     *
     * @param id 用户ID
     */
    void disableUser(Long id);

    /**
     * 修改密码
     *
     * @param id                    用户ID
     * @param changePasswordRequest 修改密码请求
     */
    void changePassword(Long id, UserDTO.ChangePasswordRequest changePasswordRequest);

    /**
     * 分页查询用户
     *
     * @param queryRequest 查询请求
     * @return 用户分页数据
     */
    PageResult<UserDTO> getUsers(UserDTO.QueryRequest queryRequest);

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
     * 获取启用用户数量
     *
     * @return 启用用户数量
     */
    long getEnabledUserCount();

    /**
     * 将User实体转换为UserDTO
     *
     * @param user 用户实体
     * @return 用户DTO
     */
    UserDTO convertToDTO(User user);

    /**
     * 将UserDTO转换为User实体
     *
     * @param userDTO 用户DTO
     * @return 用户实体
     */
    User convertToEntity(UserDTO userDTO);
}
