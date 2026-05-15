package com.linkfit.admin.exception;

import com.linkfit.admin.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("리소스를 찾을 수 없습니다."));
        }
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 오류가 발생했습니다: " + ex.getMessage()));
        }
        return "error/500";
    }
}
