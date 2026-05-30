package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Staff;
import com.linkfit.admin.mapper.StaffMapper;
import com.linkfit.admin.service.StaffService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile("dev")
public class MyBatisStaffService implements StaffService {

    private final StaffMapper staffMapper;

    public MyBatisStaffService(StaffMapper staffMapper) {
        this.staffMapper = staffMapper;
    }

    @Override
    public List<Staff> findAll(String role, int page, int size) {
        return staffMapper.findAll(role, page * size, size);
    }

    @Override
    public long count(String role) {
        return staffMapper.count(role);
    }

    @Override
    public Optional<Staff> findById(String id) {
        return staffMapper.findById(id);
    }

    @Override
    public Staff save(Staff staff) {
        if (staff.getId() == null || staff.getId().isBlank()) {
            staff.setId(UUID.randomUUID().toString());
        }
        staffMapper.insertUser(staff);
        staffMapper.insertProfile(staff);
        return staff;
    }

    @Override
    public Staff update(String id, Staff staff) {
        staff.setId(id);
        staffMapper.update(staff);
        return staff;
    }

    @Override
    public void delete(String id) {
        staffMapper.delete(id);
    }

    @Override
    public void updateRole(String id, String role) {
        staffMapper.updateRole(id, role);
    }
}
