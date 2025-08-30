package com.kinkle.helloquick.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 角色数据传输对象
 * <p>
 * 用于角色相关的数据传输，包含验证注解。
 * 遵循spring-architect.mdc的DTO设计规范。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户数量（统计信息）
     */
    private Long userCount;

    /**
     * 创建角色请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "角色名称不能为空")
        @Size(max = 50, message = "角色名称长度不能超过50个字符")
        private String roleName;

        @NotBlank(message = "角色编码不能为空")
        @Size(max = 50, message = "角色编码长度不能超过50个字符")
        private String roleCode;

        @Size(max = 500, message = "角色描述长度不能超过500个字符")
        private String description;
    }

    /**
     * 更新角色请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "角色名称不能为空")
        @Size(max = 50, message = "角色名称长度不能超过50个字符")
        private String roleName;

        @Size(max = 500, message = "角色描述长度不能超过500个字符")
        private String description;
    }

    /**
     * 角色查询请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueryRequest {

        private String roleName;
        private String roleCode;
        private Integer status;
        
        @Builder.Default
        private int page = 1;
        
        @Builder.Default
        private int size = 10;
    }
}