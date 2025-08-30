package com.kinkle.helloquick.common.exception;

import com.kinkle.helloquick.common.result.Result;
import com.kinkle.helloquick.common.result.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler单元测试
 * 
 * @author Hello Quick Team
 * @version 0.0.1-SNAPSHOT
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler单元测试")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * 辅助方法：安全获取响应体并进行非空验证
     */
    private Result<Void> getResponseBodySafely(ResponseEntity<Result<Void>> response) {
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    @Nested
    @DisplayName("业务异常处理测试")
    class BusinessExceptionHandlerTests {

        @Test
        @DisplayName("应该正确处理BusinessException")
        void shouldHandleBusinessExceptionCorrectly() {
            // Given
            BusinessException exception = BusinessException.dataNotFound("用户");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleBusinessException(exception);

                    // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Result<Void> body = getResponseBodySafely(response);
        assertThat(body.getCode()).isEqualTo(ResultCode.DATA_NOT_FOUND.getCode());
        assertThat(body.getMessage()).isEqualTo("用户不存在");
        assertThat(body.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("应该处理不同类型的BusinessException")
        void shouldHandleDifferentBusinessExceptions() {
            // Given
            BusinessException dataExistsException = BusinessException.dataExists("用户名");
            BusinessException paramErrorException = BusinessException.paramError("参数无效");
            BusinessException unauthorizedException = BusinessException.unauthorized();

            // When
            ResponseEntity<Result<Void>> dataExistsResponse = globalExceptionHandler.handleBusinessException(dataExistsException);
            ResponseEntity<Result<Void>> paramErrorResponse = globalExceptionHandler.handleBusinessException(paramErrorException);
            ResponseEntity<Result<Void>> unauthorizedResponse = globalExceptionHandler.handleBusinessException(unauthorizedException);

            // Then
            assertThat(getResponseBodySafely(dataExistsResponse).getCode()).isEqualTo(ResultCode.DATA_EXISTS.getCode());
            assertThat(getResponseBodySafely(paramErrorResponse).getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
            assertThat(getResponseBodySafely(unauthorizedResponse).getCode()).isEqualTo(ResultCode.UNAUTHORIZED.getCode());
        }
    }

    @Nested
    @DisplayName("参数验证异常处理测试")
    class ValidationExceptionHandlerTests {

        @Test
        @DisplayName("应该正确处理MethodArgumentNotValidException")
        void shouldHandleMethodArgumentNotValidExceptionCorrectly() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError1 = new FieldError("user", "username", "用户名不能为空");
            FieldError fieldError2 = new FieldError("user", "email", "邮箱格式不正确");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleValidationException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    Result<Void> body = getResponseBodySafely(response);
        assertThat(body.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
            assertThat(body.getMessage()).contains("用户名不能为空");
            assertThat(body.getMessage()).contains("邮箱格式不正确");
        }

        @Test
        @DisplayName("应该处理单个字段验证错误")
        void shouldHandleSingleFieldValidationError() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError = new FieldError("user", "password", "密码长度不能少于6位");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleValidationException(exception);

            // Then
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getMessage()).isEqualTo("密码长度不能少于6位");
        }

        @Test
        @DisplayName("应该正确处理BindException")
        void shouldHandleBindExceptionCorrectly() {
            // Given
            BindException exception = mock(BindException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError = new FieldError("user", "age", "年龄必须是数字");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleBindException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
            assertThat(body.getMessage()).isEqualTo("年龄必须是数字");
        }
    }

    @Nested
    @DisplayName("系统异常处理测试")
    class SystemExceptionHandlerTests {

        @Test
        @DisplayName("应该正确处理IllegalArgumentException")
        void shouldHandleIllegalArgumentExceptionCorrectly() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("非法的参数值");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
            assertThat(body.getMessage()).isEqualTo("非法的参数值");
        }

        @Test
        @DisplayName("应该正确处理RuntimeException")
        void shouldHandleRuntimeExceptionCorrectly() {
            // Given
            RuntimeException exception = new RuntimeException("运行时错误");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleRuntimeException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    Result<Void> body = getResponseBodySafely(response);
        assertThat(body.getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(body.getMessage()).isEqualTo("系统内部错误");
        }

        @Test
        @DisplayName("应该正确处理通用Exception")
        void shouldHandleGenericExceptionCorrectly() {
            // Given
            Exception exception = new Exception("未知错误");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    Result<Void> body = getResponseBodySafely(response);
        assertThat(body.getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(body.getMessage()).isEqualTo("系统内部错误");
        }
    }

    @Nested
    @DisplayName("异常处理优先级测试")
    class ExceptionHandlerPriorityTests {

        @Test
        @DisplayName("BusinessException应该优先于RuntimeException处理")
        void businessExceptionShouldTakePrecedenceOverRuntimeException() {
            // Given
            BusinessException businessException = BusinessException.dataNotFound("用户");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleBusinessException(businessException);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(getResponseBodySafely(response).getCode()).isEqualTo(ResultCode.DATA_NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("IllegalArgumentException应该优先于RuntimeException处理")
        void illegalArgumentExceptionShouldTakePrecedenceOverRuntimeException() {
            // Given
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("参数错误");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(getResponseBodySafely(response).getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
        }
    }

    @Nested
    @DisplayName("响应格式测试")
    class ResponseFormatTests {

        @Test
        @DisplayName("所有异常响应都应该有统一的格式")
        void allExceptionResponsesShouldHaveUniformFormat() {
            // Given
            BusinessException businessException = BusinessException.dataNotFound("用户");
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("参数错误");
            RuntimeException runtimeException = new RuntimeException("运行时错误");

            // When
            ResponseEntity<Result<Void>> businessResponse = globalExceptionHandler.handleBusinessException(businessException);
            ResponseEntity<Result<Void>> illegalArgResponse = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException);
            ResponseEntity<Result<Void>> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException);

            // Then
            // 验证所有响应都有相同的结构
            Result<Void> businessBody = getResponseBodySafely(businessResponse);
            assertThat(businessBody.getCode()).isNotNull();
            assertThat(businessBody.getMessage()).isNotNull();
            assertThat(businessBody.isSuccess()).isFalse();

            Result<Void> illegalArgBody = getResponseBodySafely(illegalArgResponse);
            assertThat(illegalArgBody.getCode()).isNotNull();
            assertThat(illegalArgBody.getMessage()).isNotNull();
            assertThat(illegalArgBody.isSuccess()).isFalse();

            Result<Void> runtimeBody = getResponseBodySafely(runtimeResponse);
            assertThat(runtimeBody.getCode()).isNotNull();
            assertThat(runtimeBody.getMessage()).isNotNull();
            assertThat(runtimeBody.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("异常响应不应该包含敏感信息")
        void exceptionResponsesShouldNotContainSensitiveInfo() {
            // Given
            RuntimeException exception = new RuntimeException("Database connection failed: password=secret123");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleRuntimeException(exception);

            // Then
            // 验证响应消息不包含敏感信息
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getMessage()).isEqualTo("系统内部错误");
            assertThat(body.getMessage()).doesNotContain("password");
            assertThat(body.getMessage()).doesNotContain("secret123");
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理null消息的异常")
        void shouldHandleExceptionWithNullMessage() {
            // Given
            RuntimeException exception = new RuntimeException((String) null);

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleRuntimeException(exception);

            // Then
            assertThat(getResponseBodySafely(response).getMessage()).isEqualTo("系统内部错误");
        }

        @Test
        @DisplayName("应该处理空字段错误列表")
        void shouldHandleEmptyFieldErrorList() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleValidationException(exception);

            // Then
            assertThat(getResponseBodySafely(response).getMessage()).isEmpty();
        }
    }
}

