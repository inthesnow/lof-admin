package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReRegistration {
    private String id;
    private String memberId;
    private String memberName;
    private String memberPhone;
    private Long gymId;
    private String reason;       // membership_expiry, pt_low, low_routine, low_app_usage, feedback_history
    private String status;       // pending, in_progress, success, failed, hold
    private String assignedTo;
    private String assignedToName;
    private String memo;
    private LocalDateTime scheduledAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDate membershipEnd;  // 최근 회원권 만료일 (유효/만료 판정 기준)
    private String productName;       // 최근 등록 상품명
    private String tier;              // 구독권 등급 (LIGHT_FIT/REGULAR_FIT/INTENSIVE_FIT)

    public ReRegistration() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDate getMembershipEnd() { return membershipEnd; }
    public void setMembershipEnd(LocalDate membershipEnd) { this.membershipEnd = membershipEnd; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
