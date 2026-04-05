package com.findu.common.exception;

import com.findu.common.response.ApiResponse;
import com.findu.common.trace.TraceIdHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        LOGGER.warn("BusinessException: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage(), ex.getContext());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        FieldError field = ex.getBindingResult().getFieldErrors().get(0);
        LOGGER.warn("Validation failed: field={}, rejected={}", field.getField(), field.getRejectedValue());
        return ApiResponse.error(CommonErrorCode.PARAM_INVALID,
            Map.of("field", field.getField(),
                   "constraint", field.getDefaultMessage() != null ? field.getDefaultMessage() : "unknown",
                   "actual", String.valueOf(field.getRejectedValue())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        LOGGER.warn("Missing parameter: {}", ex.getParameterName());
        return ApiResponse.error(CommonErrorCode.PARAM_MISSING,
            Map.of("field", ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        LOGGER.warn("Type mismatch: field={}, value={}", ex.getName(), ex.getValue());
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        return ApiResponse.error(CommonErrorCode.PARAM_INVALID,
            Map.of("field", ex.getName(),
                   "constraint", "expected type: " + expectedType,
                   "actual", String.valueOf(ex.getValue())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.warn("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponse.error(CommonErrorCode.PARAM_INVALID,
            Map.of("detail", ex.getMessage() != null ? ex.getMessage() : "unknown"));
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknown(Exception ex) {
        String traceId = TraceIdHolder.get();
        LOGGER.error("Unhandled exception, traceId={}", traceId, ex);
        return ApiResponse.error(CommonErrorCode.INTERNAL_ERROR,
            Map.of("traceId", traceId != null ? traceId : "unknown"));
    }
}
