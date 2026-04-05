package com.findu.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.findu.common.exception.CommonErrorCode;
import com.findu.common.exception.ErrorCode;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private String howto;
    private Map<String, Object> context;

    public ApiResponse() {}

    private ApiResponse(String code, String message, T data, String howto, Map<String, Object> context) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.howto = howto;
        this.context = context;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "success", data, null, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>("OK", "success", null, null, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(),
                null, errorCode.getHowto(), null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, Map<String, Object> context) {
        String filledHowto = CommonErrorCode.fillHowto(errorCode.getHowto(), context);
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(),
                null, filledHowto, context);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, String message, Map<String, Object> context) {
        String filledHowto = CommonErrorCode.fillHowto(errorCode.getHowto(), context);
        return new ApiResponse<>(errorCode.getCode(), message, null, filledHowto, context);
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getHowto() { return howto; }
    public void setHowto(String howto) { this.howto = howto; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
