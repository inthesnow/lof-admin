package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberFreeze {
    private Long id;
    private String memberId;
    private String memberName;
    private LocalDate freezeStart;
    private LocalDate freezeEnd;
    private String reason;
    private LocalDateTime createdAt;

    public MemberFreeze() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public LocalDate getFreezeStart() { return freezeStart; }
    public void setFreezeStart(LocalDate freezeStart) { this.freezeStart = freezeStart; }
    public LocalDate getFreezeEnd() { return freezeEnd; }
    public void setFreezeEnd(LocalDate freezeEnd) { this.freezeEnd = freezeEnd; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
