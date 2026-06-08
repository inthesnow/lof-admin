package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.service.AttendanceService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockAttendanceService implements AttendanceService {

    private final Map<Long, Attendance> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockAttendanceService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"1", "김민준", "M", "MEMBERSHIP"},
            {"2", "이서연", "F", "GROUP"},
            {"3", "박지훈", "M", "MEMBERSHIP"},
            {"4", "최수아", "F", "PT"},
            {"5", "강하은", "F", "MEMBERSHIP"},
        };
        for (String[] row : data) {
            Attendance a = new Attendance();
            a.setId(seq.getAndIncrement());
            a.setMemberId(row[0]);
            a.setMemberName(row[1]);
            a.setMemberGender(row[2]);
            a.setType(row[3]);
            a.setAttendDate(LocalDate.now());
            a.setCheckInTime(LocalTime.of(9 + new Random().nextInt(10), 0));
            store.put(a.getId(), a);
        }
    }

    @Override
    public List<Attendance> findAll(String date, String period) {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Attendance> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Attendance checkIn(Attendance attendance) {
        attendance.setId(seq.getAndIncrement());
        attendance.setAttendDate(LocalDate.now());
        attendance.setCheckInTime(LocalTime.now());
        store.put(attendance.getId(), attendance);
        return attendance;
    }

    @Override
    public void cancel(Long id) { store.remove(id); }

    @Override
    public List<Attendance> findFrozen(String date) { return List.of(); }
}
