package com.linkfit.admin.domain;

import java.time.LocalDate;

public class Consult {
    private Long id;
    private String type;         // NEW, EXISTING
    private String name;
    private String phone;
    private String gender;
    private Long memberId;
    private String interest;
    private String content;
    private String result;       // REGISTERED, PENDING, NO_SHOW
    private LocalDate consultDate;
    private String staffName;

    public Consult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getInterest() { return interest; }
    public void setInterest(String interest) { this.interest = interest; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public LocalDate getConsultDate() { return consultDate; }
    public void setConsultDate(LocalDate consultDate) { this.consultDate = consultDate; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
}
