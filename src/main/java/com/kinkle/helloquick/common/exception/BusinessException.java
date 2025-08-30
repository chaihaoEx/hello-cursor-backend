package com.kinkle.helloquick.common.exception;

import com.kinkle.helloquick.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于抛出业务逻辑相关的异常，包含具体的错误码和错误信息。
 * 遵循spring-architect.mdc的异常设计原则。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 结果状态码
     */
    private final ResultCode resultCode;

    /**
     * 构造函数
     *
     * @param resultCode 结果状态码
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 结果状态码
     * @param message    自定义错误信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 结果状态码
     * @param message    自定义错误信息
     * @param cause      异常原因
     */
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    /**
     * 创建数据不存在异常
     *
     * @param entityName 实体名称
     * @return 业务异常
     */
    public static BusinessException dataNotFound(String entityName) {
        return new BusinessException(ResultCode.DATA_NOT_FOUND, entityName + "不存在");
    }

    /**
     * 创建数据已存在异常
     *
     * @param entityName 实体名称
     * @return 业务异常
     */
    public static BusinessException dataExists(String entityName) {
        return new BusinessException(ResultCode.DATA_EXISTS, entityName + "已存在");
    }

    /**
     * 创建参数错误异常
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 创建未授权异常
     *
     * @return 业务异常
     */
    public static BusinessException unauthorized() {
        return new BusinessException(ResultCode.UNAUTHORIZED);
    }

    /**
     * 创建禁止访问异常
     *
     * @return 业务异常
     */
    public static BusinessException forbidden() {
        return new BusinessException(ResultCode.FORBIDDEN);
    }
}
