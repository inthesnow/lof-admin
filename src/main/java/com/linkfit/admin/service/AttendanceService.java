package com.linkfit.admin.service;

import com.linkfit.admin.domain.Attendance;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    List<Attendance> findAll(String date, String period);
    Optional<Attendance> findById(Long id);
    Attendance checkIn(Attendance attendance);
    void cancel(Long id);
    List<Attendance> findFrozen(String date);
}
