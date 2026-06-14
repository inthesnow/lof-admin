package com.linkfit.admin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청의 입/출 로그를 남기는 필터.
 * - 요청 진입: [→] METHOD URI (IP)
 * - 요청 완료: [←] METHOD URI → STATUS (Xms)
 * 502·500 등 문제 발생 시 어느 경로에서 발생했는지 추적 가능.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("ACCESS");

    // 정적 리소스는 로그 제외 (로그 노이즈 방지)
    private static final String[] SKIP_PREFIXES = {
            "/css/", "/js/", "/images/", "/favicon", "/uploads/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (shouldSkip(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();
        String ip     = getClientIp(request);
        long   start  = System.currentTimeMillis();

        log.info("[→] {} {} ({})", method, uri, ip);

        try {
            chain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            int  status  = response.getStatus();

            if (status >= 500) {
                log.error("[←] {} {} → {} ({}ms)", method, uri, status, elapsed);
            } else if (status >= 400) {
                log.warn("[←] {} {} → {} ({}ms)", method, uri, status, elapsed);
            } else {
                log.info("[←] {} {} → {} ({}ms)", method, uri, status, elapsed);
            }
        }
    }

    private boolean shouldSkip(String uri) {
        for (String prefix : SKIP_PREFIXES) {
            if (uri.startsWith(prefix)) return true;
        }
        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
}
