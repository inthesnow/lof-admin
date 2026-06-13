package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OnepointRequest {
    private Long id;
    private String memberId;
    private String memberName;
    private String trainerId;
    private String trainerName;
    private String preferredDates;  // JSON string
    private String bodyParts;       // JSON string
    private boolean hasPain;
    private String notes;
    private String status;          // PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
    private LocalDate selectedDate;
    private String selectedTime;
    private String note;            // admin note on approve/reject
    private LocalDateTime createdAt;

    public OnepointRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getTrainerId() { return trainerId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public String getPreferredDates() { return preferredDates; }
    public void setPreferredDates(String preferredDates) { this.preferredDates = preferredDates; }
    public String getBodyParts() { return bodyParts; }
    public void setBodyParts(String bodyParts) { this.bodyParts = bodyParts; }
    public boolean isHasPain() { return hasPain; }
    public void setHasPain(boolean hasPain) { this.hasPain = hasPain; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getSelectedDate() { return selectedDate; }
    public void setSelectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; }
    public String getSelectedTime() { return selectedTime; }
    public void setSelectedTime(String selectedTime) { this.selectedTime = selectedTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
