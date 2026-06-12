package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.GymHoliday;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GymHolidayMapper {
    List<GymHoliday> findAll(@Param("year") int year);
    void insert(GymHoliday holiday);
    void delete(@Param("id") Long id);
}
