package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.ClassSession;
import com.linkfit.admin.service.ClassService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// @Service (replaced by MyBatis implementation)
public class MockClassService implements ClassService {

    private final Map<Long, ClassSession> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockClassService() {
        seed();
    }

    private void seed() {
        Object[][] data = {
            {"헬스 기초반", "GROUP", "헬스", "이트레이너", LocalTime.of(9, 0), LocalTime.of(10, 0), 15, 10},
            {"필라테스 A", "GROUP", "필라테스", "최트레이너", LocalTime.of(10, 30), LocalTime.of(11, 30), 10, 8},
            {"골프 레슨", "PT", "골프", "이트레이너", LocalTime.of(14, 0), LocalTime.of(15, 0), 1, 1},
            {"OT 신규회원", "OT", "기타", "박매니저", LocalTime.of(11, 0), LocalTime.of(11, 30), 1, 1},
            {"헬스 심화반", "GROUP", "헬스", "이트레이너", LocalTime.of(18, 0), LocalTime.of(19, 0), 20, 12},
        };
        for (Object[] row : data) {
            ClassSession cs = new ClassSession();
            cs.setId(seq.getAndIncrement());
            cs.setTitle((String) row[0]);
            cs.setType((String) row[1]);
            cs.setCategory((String) row[2]);
            cs.setTrainerName((String) row[3]);
            cs.setClassDate(LocalDate.now());
            cs.setStartTime((LocalTime) row[4]);
            cs.setEndTime((LocalTime) row[5]);
            cs.setCapacity((int) row[6]);
            cs.setEnrolled((int) row[7]);
            cs.setStatus("OPEN");
            store.put(cs.getId(), cs);
        }
    }

    @Override
    public List<ClassSession> findAll(String type, String date, int page, int size) {
        return store.values().stream()
            .filter(c -> type == null || type.isBlank() || c.getType().equals(type))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String type, String date) {
        return store.values().stream()
            .filter(c -> type == null || type.isBlank() || c.getType().equals(type))
            .count();
    }

    @Override
    public Optional<ClassSession> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public ClassSession save(ClassSession session) {
        session.setId(seq.getAndIncrement());
        session.setStatus("OPEN");
        store.put(session.getId(), session);
        return session;
    }

    @Override
    public ClassSession update(Long id, ClassSession session) {
        session.setId(id);
        store.put(id, session);
        return session;
    }

    @Override
    public void cancel(Long id) {
        Optional.ofNullable(store.get(id)).ifPresent(c -> c.setStatus("CANCELLED"));
    }

    @Override
    public void enroll(Long classId, Long memberId) {}

    @Override
    public void cancelEnrollment(Long classId, Long memberId) {}
}
