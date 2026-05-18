package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Staff;
import com.linkfit.admin.mapper.StaffMapper;
import com.linkfit.admin.service.StaffService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<Staff> findById(Long id) {
        return staffMapper.findById(id);
    }

    @Override
    public Staff save(Staff staff) {
        staffMapper.insert(staff);
        return staff;
    }

    @Override
    public Staff update(Long id, Staff staff) {
        staff.setId(id);
        staffMapper.update(staff);
        return staff;
    }

    @Override
    public void delete(Long id) {
        staffMapper.delete(id);
    }

    @Override
    public void updateRole(Long id, String role) {
        staffMapper.updateRole(id, role);
    }
}
