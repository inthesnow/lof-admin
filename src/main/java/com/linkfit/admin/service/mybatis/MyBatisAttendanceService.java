package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.domain.MemberFreeze;
import com.linkfit.admin.mapper.AttendanceMapper;
import com.linkfit.admin.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class MyBatisAttendanceService implements AttendanceService {

    private final AttendanceMapper attendanceMapper;

    public MyBatisAttendanceService(AttendanceMapper attendanceMapper) {
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public List<Attendance> findAll(String date, String period, Long gymId) {
        String d = (date == null || date.isBlank()) ? LocalDate.now().toString() : date;
        return attendanceMapper.findAll(d, period, gymId);
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        return attendanceMapper.findById(id);
    }

    @Override
    public Attendance checkIn(Attendance attendance, Long gymId) {
        if (attendance.getAttendDate() == null) attendance.setAttendDate(LocalDate.now());
        // check_in_time은 DB에서 NOT NULL인데 화면에서 입력받지 않으므로 서버에서 현재 시각으로 채움
        // (이걸 빠뜨려서 체크인 시도마다 SQL 에러로 조용히 실패하던 버그가 있었음).
        if (attendance.getCheckInTime() == null) attendance.setCheckInTime(LocalTime.now());
        attendanceMapper.checkIn(attendance, gymId);
        return attendance;
    }

    @Override
    public void cancel(Long id, Long gymId) {
        attendanceMapper.cancel(id, gymId);
    }

    @Override
    public List<Attendance> findFrozen(String date, Long gymId) {
        String d = (date == null || date.isBlank()) ? LocalDate.now().toString() : date;
        List<MemberFreeze> freezes = attendanceMapper.findFrozen(d, gymId);
        return freezes.stream().map(f -> {
            Attendance a = new Attendance();
            a.setMemberId(f.getMemberId());
            a.setMemberName(f.getMemberName());
            a.setType("FREEZE");
            a.setAttendDate(f.getFreezeStart());
            return a;
        }).toList();
    }
}
