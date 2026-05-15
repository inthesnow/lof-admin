package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClassSession {
    private Long id;
    private String title;
    private String type;         // GROUP, PT, OT
    private String category;     // 헬스, 필라테스, 골프, 기타
    private Long trainerId;
    private String trainerName;
    private LocalDate classDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int enrolled;
    private String status;       // OPEN, CANCELLED

    public ClassSession() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public LocalDate getClassDate() { return classDate; }
    public void setClassDate(LocalDate classDate) { this.classDate = classDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
