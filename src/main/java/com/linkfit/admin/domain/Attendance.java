package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberGender;
    private String type;         // MEMBERSHIP, GROUP, PT
    private LocalDate attendDate;
    private LocalTime checkInTime;

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberGender() { return memberGender; }
    public void setMemberGender(String memberGender) { this.memberGender = memberGender; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getAttendDate() { return attendDate; }
    public void setAttendDate(LocalDate attendDate) { this.attendDate = attendDate; }
    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }
}
