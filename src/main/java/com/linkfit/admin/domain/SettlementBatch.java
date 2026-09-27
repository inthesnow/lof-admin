package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 이 지점이 최고관리자(lof-potal)로부터 받을(또는 받은) 정산 배치 — 조회 전용.
 * 실제 계산/지급 처리는 lof-potal이 담당하며, 이 지점 관리자는 결과만 열람한다.
 */
public class SettlementBatch {
    private Long id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private int grossTotal;
    private int netTotal;
    private int adjustmentAmount;
    private int finalAmount;
    private String status;
    private LocalDateTime confirmedAt;
    private LocalDateTime paidAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public int getGrossTotal() { return grossTotal; }
    public void setGrossTotal(int grossTotal) { this.grossTotal = grossTotal; }
    public int getNetTotal() { return netTotal; }
    public void setNetTotal(int netTotal) { this.netTotal = netTotal; }
    public int getAdjustmentAmount() { return adjustmentAmount; }
    public void setAdjustmentAmount(int adjustmentAmount) { this.adjustmentAmount = adjustmentAmount; }
    public int getFinalAmount() { return finalAmount; }
    public void setFinalAmount(int finalAmount) { this.finalAmount = finalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
