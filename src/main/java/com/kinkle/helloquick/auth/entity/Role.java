package com.kinkle.helloquick.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * <p>
 * 映射数据库中的roles表。
 * 遵循spring-architect.mdc原则：简单实体，无复杂关联。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Integer status = 1;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 判断角色是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}