package com.linkfit.admin.exception;

import com.linkfit.admin.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("[404] {} {}", request.getMethod(), request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("[404] {} {}", request.getMethod(), request.getRequestURI());
        if (isJsonRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("리소스를 찾을 수 없습니다."));
        }
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("[500] {} {} — {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        if (isJsonRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
        return "error/500";
    }

    /// `/api/**` 요청은 Accept 헤더와 무관하게 항상 JSON으로 응답해야 한다.
    /// 화면들의 `csrfHeaders()`가 Content-Type만 넣고 Accept는 안 보내서, 브라우저 기본
    /// Accept(text/html...)로 나가는 탓에 이 판정이 항상 false가 됐다 — 그 결과 API 호출에서
    /// 예외가 나면 500 대신 Thymeleaf `error/500` 뷰가 **HTTP 200 + HTML**로 내려가고,
    /// 프론트의 `await res.json()`이 파싱 실패로 예외를 던져 alert조차 못 뜨고 아무 일도
    /// 안 일어난 것처럼 보였다("저장은 된 것 같은데 적용이 안 됨" 증상의 공통 원인, 2026-11-08).
    private boolean isJsonRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/")) return true;
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }
}
