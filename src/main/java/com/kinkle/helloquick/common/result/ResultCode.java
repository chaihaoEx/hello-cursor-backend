package com.kinkle.helloquick.common.result;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 * <p>
 * 定义了系统中所有可能的响应状态码，包括成功、客户端错误、服务器错误等。
 * 遵循HTTP状态码规范，便于前端统一处理。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAILURE(400, "操作失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(410, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(409, "数据已存在"),

    /**
     * 参数错误
     */
    PARAM_ERROR(422, "参数错误"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 状态消息
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
