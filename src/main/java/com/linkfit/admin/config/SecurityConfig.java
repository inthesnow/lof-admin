package com.linkfit.admin.config;

import com.linkfit.admin.security.JwtCookieFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtCookieFilter jwtCookieFilter;

    public SecurityConfig(JwtCookieFilter jwtCookieFilter) {
        this.jwtCookieFilter = jwtCookieFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/api/auth/login", "/api/auth/logout",
                    "/css/**", "/js/**", "/favicon.svg", "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String accept = request.getHeader("Accept");
                    if (accept != null && accept.contains("application/json")) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    } else {
                        response.sendRedirect("/login");
                    }
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
