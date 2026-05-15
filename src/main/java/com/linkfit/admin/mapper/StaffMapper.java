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
    Optional<Staff> findById(@Param("id") Long id);
    void insert(Staff staff);
    void update(Staff staff);
    void delete(@Param("id") Long id);
    void updateRole(@Param("id") Long id, @Param("role") String role);
}
