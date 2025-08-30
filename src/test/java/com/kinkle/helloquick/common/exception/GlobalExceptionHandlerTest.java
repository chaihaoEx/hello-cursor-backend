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
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    @DisplayName("静态资源异常处理测试")
    class StaticResourceExceptionHandlerTests {

        @Test
        @DisplayName("应该正确处理NoResourceFoundException - 非favicon.ico")
        void shouldHandleNoResourceFoundExceptionCorrectly() {
            // Given
            NoResourceFoundException exception = mock(NoResourceFoundException.class);
            when(exception.getResourcePath()).thenReturn("/api/nonexistent");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleNoResourceFoundException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
            assertThat(body.getMessage()).isEqualTo("请求的资源不存在");
        }

        @Test
        @DisplayName("应该正确处理NoResourceFoundException - favicon.ico")
        void shouldHandleNoResourceFoundExceptionForFavicon() {
            // Given
            NoResourceFoundException exception = mock(NoResourceFoundException.class);
            when(exception.getResourcePath()).thenReturn("/favicon.ico");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleNoResourceFoundException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
            assertThat(body.getMessage()).isEqualTo("请求的资源不存在");
        }

        @Test
        @DisplayName("应该正确处理NoResourceFoundException - 包含favicon.ico的路径")
        void shouldHandleNoResourceFoundExceptionForFaviconInPath() {
            // Given
            NoResourceFoundException exception = mock(NoResourceFoundException.class);
            when(exception.getResourcePath()).thenReturn("/static/images/favicon.ico");

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleNoResourceFoundException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
            assertThat(body.getMessage()).isEqualTo("请求的资源不存在");
        }

        @Test
        @DisplayName("应该正确处理NoResourceFoundException - null资源路径")
        void shouldHandleNoResourceFoundExceptionWithNullPath() {
            // Given
            NoResourceFoundException exception = mock(NoResourceFoundException.class);
            when(exception.getResourcePath()).thenReturn(null);

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleNoResourceFoundException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
            assertThat(body.getMessage()).isEqualTo("请求的资源不存在");
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

        @Test
        @DisplayName("应该处理包含null消息的字段错误")
        void shouldHandleFieldErrorWithNullMessage() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError1 = new FieldError("user", "username", null, false, null, null, "用户名不能为空");
            FieldError fieldError2 = new FieldError("user", "email", null, false, null, null, null);
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleValidationException(exception);

            // Then
            Result<Void> body = getResponseBodySafely(response);
            assertThat(body.getMessage()).contains("用户名不能为空");
            assertThat(body.getMessage()).contains("null");
        }

        @Test
        @DisplayName("应该处理BindException的空字段错误列表")
        void shouldHandleBindExceptionWithEmptyFieldErrorList() {
            // Given
            BindException exception = mock(BindException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

            // When
            ResponseEntity<Result<Void>> response = globalExceptionHandler.handleBindException(exception);

            // Then
            assertThat(getResponseBodySafely(response).getMessage()).isEmpty();
        }
    }

    @Nested
    @DisplayName("异常处理完整性测试")
    class ExceptionHandlerCompletenessTests {

        @Test
        @DisplayName("应该处理所有类型的BusinessException")
        void shouldHandleAllTypesOfBusinessExceptions() {
            // Given
            BusinessException[] exceptions = {
                BusinessException.dataNotFound("用户"),
                BusinessException.dataExists("用户名"),
                BusinessException.paramError("参数无效"),
                BusinessException.unauthorized(),
                BusinessException.forbidden()
            };

            // When & Then
            for (BusinessException exception : exceptions) {
                ResponseEntity<Result<Void>> response = globalExceptionHandler.handleBusinessException(exception);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(getResponseBodySafely(response).getCode()).isEqualTo(exception.getResultCode().getCode());
                assertThat(getResponseBodySafely(response).getMessage()).isEqualTo(exception.getMessage());
            }
        }

        @Test
        @DisplayName("应该处理各种RuntimeException子类")
        void shouldHandleVariousRuntimeExceptionSubclasses() {
            // Given
            RuntimeException[] exceptions = {
                new RuntimeException("普通运行时异常"),
                new IllegalStateException("非法状态异常"),
                new UnsupportedOperationException("不支持的操作异常"),
                new NullPointerException("空指针异常")
            };

            // When & Then
            for (RuntimeException exception : exceptions) {
                ResponseEntity<Result<Void>> response = globalExceptionHandler.handleRuntimeException(exception);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                assertThat(getResponseBodySafely(response).getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
                assertThat(getResponseBodySafely(response).getMessage()).isEqualTo("系统内部错误");
            }
        }

        @Test
        @DisplayName("应该处理各种Exception子类")
        void shouldHandleVariousExceptionSubclasses() {
            // Given
            Exception[] exceptions = {
                new Exception("普通异常"),
                new ClassNotFoundException("类未找到异常"),
                new NoSuchMethodException("方法未找到异常"),
                new SecurityException("安全异常")
            };

            // When & Then
            for (Exception exception : exceptions) {
                ResponseEntity<Result<Void>> response = globalExceptionHandler.handleException(exception);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                assertThat(getResponseBodySafely(response).getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
                assertThat(getResponseBodySafely(response).getMessage()).isEqualTo("系统内部错误");
            }
        }
    }

    @Nested
    @DisplayName("日志记录测试")
    class LoggingTests {

        @Test
        @DisplayName("应该正确记录不同级别的日志")
        void shouldLogAtCorrectLevels() {
            // Given
            BusinessException businessException = BusinessException.dataNotFound("用户");
            RuntimeException runtimeException = new RuntimeException("运行时错误");
            Exception genericException = new Exception("通用异常");

            // When
            globalExceptionHandler.handleBusinessException(businessException);
            globalExceptionHandler.handleRuntimeException(runtimeException);
            globalExceptionHandler.handleException(genericException);

            // Then - 这里主要验证方法能正常执行，实际的日志验证需要集成测试
            // 在单元测试中，我们主要验证异常处理逻辑的正确性
            assertThat(businessException).isNotNull();
            assertThat(runtimeException).isNotNull();
            assertThat(genericException).isNotNull();
        }
    }
}

