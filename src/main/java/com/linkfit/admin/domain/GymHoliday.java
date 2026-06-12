package com.linkfit.admin.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GymHoliday {
    private Long id;
    private LocalDate holidayDate;
    private String type;   // PUBLIC, CLOSURE
    private String name;
    private LocalDateTime createdAt;

    public GymHoliday() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public void setHolidayDate(LocalDate holidayDate) { this.holidayDate = holidayDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
