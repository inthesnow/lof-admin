package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.util.List;

// 회원 목록 조회(findAll/count/export)에 쓰이는 검색·필터 조건 묶음.
// 필터가 하나씩 늘면서 메서드 파라미터 목록이 계속 길어지던 걸 여기로 모음.
public class MemberFilter {
    private String keyword;
    private String status;
    private String tier;
    private List<String> trainerIds;
    private Integer minDaysLeft;
    private Integer maxDaysLeft;
    private Integer minPtRemaining;
    private Integer minAbsentDays;
    private Boolean appInstalled;
    private LocalDate joinDateFrom;
    private LocalDate joinDateTo;
    private LocalDate membershipEndFrom;
    private LocalDate membershipEndTo;
    private Integer minUnpaidAmount;
    private boolean excludeTrainers;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public List<String> getTrainerIds() { return trainerIds; }
    public void setTrainerIds(List<String> trainerIds) { this.trainerIds = trainerIds; }
    public Integer getMinDaysLeft() { return minDaysLeft; }
    public void setMinDaysLeft(Integer minDaysLeft) { this.minDaysLeft = minDaysLeft; }
    public Integer getMaxDaysLeft() { return maxDaysLeft; }
    public void setMaxDaysLeft(Integer maxDaysLeft) { this.maxDaysLeft = maxDaysLeft; }
    public Integer getMinPtRemaining() { return minPtRemaining; }
    public void setMinPtRemaining(Integer minPtRemaining) { this.minPtRemaining = minPtRemaining; }
    public Integer getMinAbsentDays() { return minAbsentDays; }
    public void setMinAbsentDays(Integer minAbsentDays) { this.minAbsentDays = minAbsentDays; }
    public Boolean getAppInstalled() { return appInstalled; }
    public void setAppInstalled(Boolean appInstalled) { this.appInstalled = appInstalled; }
    public LocalDate getJoinDateFrom() { return joinDateFrom; }
    public void setJoinDateFrom(LocalDate joinDateFrom) { this.joinDateFrom = joinDateFrom; }
    public LocalDate getJoinDateTo() { return joinDateTo; }
    public void setJoinDateTo(LocalDate joinDateTo) { this.joinDateTo = joinDateTo; }
    public LocalDate getMembershipEndFrom() { return membershipEndFrom; }
    public void setMembershipEndFrom(LocalDate membershipEndFrom) { this.membershipEndFrom = membershipEndFrom; }
    public LocalDate getMembershipEndTo() { return membershipEndTo; }
    public void setMembershipEndTo(LocalDate membershipEndTo) { this.membershipEndTo = membershipEndTo; }
    public Integer getMinUnpaidAmount() { return minUnpaidAmount; }
    public void setMinUnpaidAmount(Integer minUnpaidAmount) { this.minUnpaidAmount = minUnpaidAmount; }
    public boolean isExcludeTrainers() { return excludeTrainers; }
    public void setExcludeTrainers(boolean excludeTrainers) { this.excludeTrainers = excludeTrainers; }
}
