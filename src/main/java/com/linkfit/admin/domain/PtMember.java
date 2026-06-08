package com.linkfit.admin.domain;

public class PtMember {
    private String memberId;
    private String memberName;
    private String memberPhone;
    private String tier;
    private int ptRemaining;
    private String trainerName;

    public PtMember() {}

    public String getMemberId()   { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
    public String getTier()       { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public int getPtRemaining()   { return ptRemaining; }
    public void setPtRemaining(int ptRemaining) { this.ptRemaining = ptRemaining; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
}
