package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class GymBanner {
    private Long id;
    private String imageUrl;
    private String title;
    private boolean isActive;
    private int sortOrder;
    private LocalDateTime createdAt;

    public GymBanner() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
