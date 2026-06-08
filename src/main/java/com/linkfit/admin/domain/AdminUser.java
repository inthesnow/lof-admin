package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class AdminUser {
    private Long id;
    private Long gymId;
    private String branchCode;
    private String username;
    private String password;
    private String name;
    private String role;   // SUPER_ADMIN, ADMIN, TRAINER
    private boolean active;
    private LocalDateTime createdAt;

    public AdminUser() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
