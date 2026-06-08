package com.linkfit.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final String cookieName;

    public JwtCookieFilter(JwtUtil jwtUtil,
                           @Value("${app.jwt.cookie-name}") String cookieName) {
        this.jwtUtil    = jwtUtil;
        this.cookieName = cookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtUtil.isValid(token)) {
            String userId     = jwtUtil.getSubject(token);
            String role       = jwtUtil.getRole(token);
            String branchCode = jwtUtil.getBranchCode(token);
            String username   = jwtUtil.getUsername(token);
            Long   gymId      = jwtUtil.getGymId(token);

            CrmUserDetails userDetails = new CrmUserDetails(
                    userId, branchCode, username, gymId, role);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // 1. HttpOnly 쿠키에서 추출 (Thymeleaf SSR용)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 2. Authorization 헤더에서 추출 (REST 클라이언트용)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
