package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.domain.MemberFreeze;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface AttendanceMapper {
    List<Attendance> findAll(@Param("date") String date, @Param("period") String period, @Param("gymId") Long gymId);
    Optional<Attendance> findById(@Param("id") Long id);
    void checkIn(@Param("attendance") Attendance attendance, @Param("gymId") Long gymId);
    // 수동 체크인 전 중복 방지 — 앱 쪽 자동 출석 체크(운동기록/PT/원포인트 완료)와 같은 "하루 1건" 원칙을 공유
    int existsForDate(@Param("memberId") String memberId, @Param("gymId") Long gymId, @Param("attendDate") LocalDate attendDate);
    void cancel(@Param("id") Long id, @Param("gymId") Long gymId);
    List<MemberFreeze> findFrozen(@Param("date") String date, @Param("gymId") Long gymId);
    Map<String, Object> countStats(@Param("date") String date, @Param("period") String period,
                                   @Param("type") String type, @Param("gymId") Long gymId);
    Map<String, Object> countFrozen(@Param("date") String date, @Param("gymId") Long gymId);

    List<Map<String, Object>> dailyTrend(@Param("startDate") String startDate,
                                          @Param("endDate") String endDate, @Param("gymId") Long gymId);

    // 회원별 출석 통계 — date 기준으로 daily(해당일)/weekly(해당 주)/monthly(해당 월) 범위 집계
    List<Map<String, Object>> memberPeriodStats(@Param("date") String date, @Param("period") String period,
                                                 @Param("gymId") Long gymId);

    // 장기 미출석 회원
    List<Map<String, Object>> inactiveMembers(@Param("days") int days, @Param("gymId") Long gymId);

    // 유증 등록/삭제
    void addFreeze(@Param("freeze") MemberFreeze freeze, @Param("gymId") Long gymId);
    void deleteFreeze(@Param("id") Long id, @Param("gymId") Long gymId);
}
