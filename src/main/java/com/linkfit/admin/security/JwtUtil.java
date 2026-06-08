package com.linkfit.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration) {
        this.secretKey  = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(String userId, String branchCode, String username, String role, Long gymId) {
        return Jwts.builder()
                .subject(userId)
                .claim("branchCode", branchCode)
                .claim("username",   username)
                .claim("role",       role)
                .claim("gymId",      gymId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long getGymId(String token) {
        return parseClaims(token).get("gymId", Long.class);
    }

    public String getBranchCode(String token) {
        return parseClaims(token).get("branchCode", String.class);
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }
}
