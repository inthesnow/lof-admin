package com.linkfit.admin.service;

import com.linkfit.admin.domain.ClassSession;
import java.util.List;
import java.util.Optional;

public interface ClassService {
    List<ClassSession> findAll(String type, String date, int page, int size);
    long count(String type, String date);
    Optional<ClassSession> findById(Long id);
    ClassSession save(ClassSession session);
    ClassSession update(Long id, ClassSession session);
    void cancel(Long id);
    void enroll(Long classId, String memberId);
    void cancelEnrollment(Long classId, String memberId);
}
