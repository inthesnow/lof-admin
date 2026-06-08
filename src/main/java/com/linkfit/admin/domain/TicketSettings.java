package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class TicketSettings {
    private String id;
    private Long gymId;
    private int freeTicketsPerMember;
    private Integer maxTicketsPerMonth;
    private boolean isBeta;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public TicketSettings() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public int getFreeTicketsPerMember() { return freeTicketsPerMember; }
    public void setFreeTicketsPerMember(int freeTicketsPerMember) { this.freeTicketsPerMember = freeTicketsPerMember; }
    public Integer getMaxTicketsPerMonth() { return maxTicketsPerMonth; }
    public void setMaxTicketsPerMonth(Integer maxTicketsPerMonth) { this.maxTicketsPerMonth = maxTicketsPerMonth; }
    public boolean isBeta() { return isBeta; }
    public void setBeta(boolean beta) { isBeta = beta; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
