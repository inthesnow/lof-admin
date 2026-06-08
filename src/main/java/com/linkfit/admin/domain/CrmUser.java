package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class CrmUser {
    private String id;           // CHAR(36) UUID
    private Long gymId;
    private String branchCode;   // gym.branch_code (JOIN용)
    private String appUserId;    // users.user_id 연결 (트레이너 선택적)
    private String name;
    private String email;
    private String username;
    private String passwordHash;
    private String role;         // super_admin | gym_admin | trainer
    private boolean active;
    private LocalDateTime createdAt;

    public CrmUser() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getAppUserId() { return appUserId; }
    public void setAppUserId(String appUserId) { this.appUserId = appUserId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
