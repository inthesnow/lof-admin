package com.linkfit.admin.domain;

import java.time.LocalDateTime;

public class ClassAttendee {
    private Long id;
    private Long classSessionId;
    private Long memberId;
    private String memberName;
    private String phone;
    private String gender;
    private LocalDateTime registeredAt;
    private boolean attended;

    public ClassAttendee() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClassSessionId() { return classSessionId; }
    public void setClassSessionId(Long classSessionId) { this.classSessionId = classSessionId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }
}
