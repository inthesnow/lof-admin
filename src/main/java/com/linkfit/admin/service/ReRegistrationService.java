package com.linkfit.admin.service;

import com.linkfit.admin.domain.ReRegistration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReRegistrationService {
    List<ReRegistration> findAll(Long gymId, String status, String reason, int page, int size);
    long count(Long gymId, String status, String reason);
    Optional<ReRegistration> findById(String id);
    void updateStatus(String id, String status);
    void updateMemo(String id, String memo);
    void assign(String id, String assignedTo);
    int autoClassify(Long gymId);         // 자동 분류 실행, 신규 생성 건수 반환
    Map<String, Integer> statusSummary(Long gymId);
}
