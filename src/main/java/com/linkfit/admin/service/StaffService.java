package com.linkfit.admin.service;

import com.linkfit.admin.domain.Staff;
import java.util.List;
import java.util.Optional;

public interface StaffService {
    List<Staff> findAll(String role, int page, int size);
    long count(String role);
    Optional<Staff> findById(String id);
    Staff save(Staff staff);
    Staff update(String id, Staff staff);
    void delete(String id);
    void updateRole(String id, String role);
}
