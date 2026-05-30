package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StaffMapper {
    List<Staff> findAll(@Param("role") String role, @Param("offset") int offset, @Param("size") int size);
    long count(@Param("role") String role);
    Optional<Staff> findById(@Param("id") String id);
    void insertUser(Staff staff);
    void insertProfile(Staff staff);
    void update(Staff staff);
    void delete(@Param("id") String id);
    void updateRole(@Param("id") String id, @Param("role") String role);
}
