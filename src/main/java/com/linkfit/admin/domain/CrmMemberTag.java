package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class CrmMemberTag {
    private String id;
    private String memberId;
    private Long gymId;
    private String tag;
    private String color;
    private LocalDateTime createdAt;

    public CrmMemberTag() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
