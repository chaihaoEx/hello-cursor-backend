package com.kinkle.helloquick.common.exception;

import com.kinkle.helloquick.common.result.Result;
import com.kinkle.helloquick.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用中的各种异常，确保响应格式的一致性。
 * 遵循spring-architect.mdc的异常处理最佳实践。
 * </p>
 *
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(e.getResultCode(), e.getMessage()));
    }

    /**
     * 处理参数验证异常
     *
     * @param e 参数验证异常
     * @return 统一响应格式
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数验证失败: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ResultCode.PARAM_ERROR, message));
    }

    /**
     * 处理绑定异常
     *
     * @param e 绑定异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("数据绑定失败: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ResultCode.PARAM_ERROR, message));
    }

    /**
     * 处理非法参数异常
     *
     * @param e 非法参数异常
     * @return 统一响应格式
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(ResultCode.PARAM_ERROR, e.getMessage()));
    }

    /**
     * 处理静态资源未找到异常
     * 常见于浏览器请求favicon.ico等静态资源
     *
     * @param e 静态资源未找到异常
     * @return 统一响应格式
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        // 对于favicon.ico等静态资源请求，记录为debug级别，不记录为错误
        if (e.getResourcePath() != null && e.getResourcePath().contains("favicon.ico")) {
            log.debug("静态资源未找到: {}", e.getResourcePath());
        } else {
            log.warn("静态资源未找到: {}", e.getResourcePath());
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.failure(ResultCode.NOT_FOUND, "请求的资源不存在"));
    }

    /**
     * 处理运行时异常
     *
     * @param e 运行时异常
     * @return 统一响应格式
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "系统内部错误"));
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param e 异常
     * @return 统一响应格式
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("未预期的异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(ResultCode.INTERNAL_SERVER_ERROR, "系统内部错误"));
    }
}
