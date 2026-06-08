package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.CrmUser;
import com.linkfit.admin.security.JwtUtil;
import com.linkfit.admin.service.CrmUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final CrmUserService crmUserService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final String cookieName;

    public AuthApiController(CrmUserService crmUserService,
                             JwtUtil jwtUtil,
                             PasswordEncoder passwordEncoder,
                             @Value("${app.jwt.cookie-name}") String cookieName) {
        this.crmUserService  = crmUserService;
        this.jwtUtil         = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.cookieName      = cookieName;
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, String> body,
                                HttpServletResponse response) {
        String branchCode = body.get("branchCode");
        String username   = body.get("username");
        String password   = body.get("password");

        if (branchCode == null || username == null || password == null) {
            return ApiResponse.error("지점코드, 아이디, 비밀번호를 모두 입력해주세요.");
        }

        Optional<CrmUser> userOpt =
                crmUserService.findByBranchCodeAndUsername(branchCode.toUpperCase(), username);

        if (userOpt.isEmpty()) {
            return ApiResponse.error("지점코드 또는 아이디가 올바르지 않습니다.");
        }

        CrmUser user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ApiResponse.error("비밀번호가 올바르지 않습니다.");
        }

        if (!user.isActive()) {
            return ApiResponse.error("비활성화된 계정입니다.");
        }

        String token = jwtUtil.generateToken(
                user.getId(), user.getBranchCode(), user.getUsername(),
                user.getRole(), user.getGymId());

        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);    // 24h
        response.addCookie(cookie);

        return ApiResponse.ok(Map.of(
                "id",         user.getId(),
                "name",       user.getName(),
                "role",       user.getRole(),
                "branchCode", user.getBranchCode(),
                "gymId",      user.getGymId()
        ));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponse.ok();
    }
}
