package com.linkfit.admin.service;

import com.linkfit.admin.domain.Staff;
import java.util.List;
import java.util.Optional;

public interface StaffService {
    List<Staff> findAll(String role, int page, int size);
    long count(String role);
    Optional<Staff> findById(Long id);
    Staff save(Staff staff);
    Staff update(Long id, Staff staff);
    void delete(Long id);
    void updateRole(Long id, String role);
}
