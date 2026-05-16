package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Membership {
    private Long id;
    private Long memberId;
    private Long productId;
    private String productName;
    private String type;   // MEMBERSHIP, GROUP, PT, LOCKER, ITEM
    private LocalDate startDate;
    private LocalDate endDate;
    private int price;
    private String memo;
    private LocalDateTime createdAt;

    public Membership() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
