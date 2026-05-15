package com.linkfit.admin.common;

public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "ok", data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, "ok", null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
