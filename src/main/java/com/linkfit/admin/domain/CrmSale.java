package com.linkfit.admin.domain;

import java.math.BigDecimal;

public class CrmSale {
    private String id;
    private Long gymId;
    private String memberId;
    private String memberName;
    private String salesType;
    private String regType;
    private String trainerId;
    private String trainerName;
    private BigDecimal amount;
    private String saleDate;
    private String note;
    private String createdAt;

    public CrmSale() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getSalesType() { return salesType; }
    public void setSalesType(String salesType) { this.salesType = salesType; }
    public String getRegType() { return regType; }
    public void setRegType(String regType) { this.regType = regType; }
    public String getTrainerId() { return trainerId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getSaleDate() { return saleDate; }
    public void setSaleDate(String saleDate) { this.saleDate = saleDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
