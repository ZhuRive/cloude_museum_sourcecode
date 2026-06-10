package com.cloudmuseum.dto;

import lombok.Data;

@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(400, message, null);
    }

    public static <T> ApiResult<T> unauthorized(String message) {
        return new ApiResult<>(401, message, null);
    }

    public static <T> ApiResult<T> forbidden(String message) {
        return new ApiResult<>(403, message, null);
    }
}
