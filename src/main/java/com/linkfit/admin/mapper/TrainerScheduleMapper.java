package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.TrainerSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrainerScheduleMapper {
    List<TrainerSchedule> findByMonth(@Param("year") int year, @Param("month") int month);
    List<TrainerSchedule> findByDate(@Param("date") String date);
}
