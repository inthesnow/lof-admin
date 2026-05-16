package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.domain.MemberFreeze;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface AttendanceMapper {
    List<Attendance> findAll(@Param("date") String date, @Param("period") String period);
    Optional<Attendance> findById(@Param("id") Long id);
    void checkIn(Attendance attendance);
    void cancel(@Param("id") Long id);
    List<MemberFreeze> findFrozen(@Param("date") String date);
    Map<String, Object> countStats(@Param("date") String date, @Param("period") String period,
                                   @Param("type") String type);
    Map<String, Object> countFrozen(@Param("date") String date);
}
