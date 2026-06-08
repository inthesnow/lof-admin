package com.linkfit.admin.service;

import com.linkfit.admin.domain.AdminUser;
import com.linkfit.admin.mapper.AdminUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("dev")
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserMapper adminUserMapper;

    public AdminUserDetailsService(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    /**
     * username 형식: "BRANCHCODE::username" (e.g. "LF01::admin")
     * 로그인 폼 JS에서 지점코드와 아이디를 합성해서 전달.
     */
    @Override
    public UserDetails loadUserByUsername(String compositeUsername) throws UsernameNotFoundException {
        String[] parts = compositeUsername.split("::", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new UsernameNotFoundException("지점코드 또는 아이디가 올바르지 않습니다.");
        }

        String branchCode = parts[0].toUpperCase();
        String username   = parts[1];

        AdminUser adminUser = adminUserMapper.findByBranchCodeAndUsername(branchCode, username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + branchCode + " / " + username));

        return new User(
                compositeUsername,
                adminUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + adminUser.getRole()))
        );
    }
}
