package com.kinkle.helloquick.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果类
 * <p>
 * 用于包装所有API接口的返回结果，提供统一的响应格式。
 * 提供统一的响应格式，便于前端统一处理。
 * </p>
 *
 * @param <T> 数据类型
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 请求ID（用于链路追踪）
     */
    private String requestId;

    /**
     * 创建成功响应
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    /**
     * 创建成功响应
     *
     * @param data    响应数据
     * @param message 自定义消息
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(T data, String message) {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param resultCode 结果状态码
     * @param <T>        数据类型
     * @return 失败响应
     */
    public static <T> Result<T> failure(ResultCode resultCode) {
        return Result.<T>builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param resultCode 结果状态码
     * @param message    自定义消息
     * @param <T>        数据类型
     * @return 失败响应
     */
    public static <T> Result<T> failure(ResultCode resultCode, String message) {
        return Result.<T>builder()
                .code(resultCode.getCode())
                .message(message)
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> Result<T> failure(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 设置请求ID
     *
     * @param requestId 请求ID
     * @return 当前对象
     */
    public Result<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
