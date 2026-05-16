package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.ClassAttendee;
import com.linkfit.admin.domain.ClassSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ClassMapper {
    List<ClassSession> findAll(@Param("type") String type, @Param("date") String date,
                               @Param("offset") int offset, @Param("size") int size);
    long count(@Param("type") String type, @Param("date") String date);
    Optional<ClassSession> findById(@Param("id") Long id);
    void insert(ClassSession session);
    void update(ClassSession session);
    void cancel(@Param("id") Long id);
    void enroll(@Param("classId") Long classId, @Param("memberId") Long memberId);
    void incrementEnrolled(@Param("id") Long id);
    void cancelEnrollment(@Param("classId") Long classId, @Param("memberId") Long memberId);
    void decrementEnrolled(@Param("id") Long id);
    List<ClassAttendee> findAttendees(@Param("classId") Long classId);
}
