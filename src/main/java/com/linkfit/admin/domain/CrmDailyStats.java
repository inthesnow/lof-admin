package com.linkfit.admin.domain;

import java.math.BigDecimal;

public class CrmDailyStats {
    private Long gymId;
    private String statDate;
    private int totalMembers;
    private int activeMembers;
    private int dormantMembers;
    private int routineCompleted;
    private BigDecimal attendanceRate;
    private int feedbackIssued;
    private int feedbackUsed;

    public CrmDailyStats() {}

    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
    public int getActiveMembers() { return activeMembers; }
    public void setActiveMembers(int activeMembers) { this.activeMembers = activeMembers; }
    public int getDormantMembers() { return dormantMembers; }
    public void setDormantMembers(int dormantMembers) { this.dormantMembers = dormantMembers; }
    public int getRoutineCompleted() { return routineCompleted; }
    public void setRoutineCompleted(int routineCompleted) { this.routineCompleted = routineCompleted; }
    public BigDecimal getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(BigDecimal attendanceRate) { this.attendanceRate = attendanceRate; }
    public int getFeedbackIssued() { return feedbackIssued; }
    public void setFeedbackIssued(int feedbackIssued) { this.feedbackIssued = feedbackIssued; }
    public int getFeedbackUsed() { return feedbackUsed; }
    public void setFeedbackUsed(int feedbackUsed) { this.feedbackUsed = feedbackUsed; }
}
