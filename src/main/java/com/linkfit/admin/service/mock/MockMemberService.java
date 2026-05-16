package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.service.MemberService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// @Service (replaced by MyBatis implementation)
public class MockMemberService implements MemberService {

    private final Map<Long, Member> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockMemberService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"김민준", "010-1234-5678", "M", "ACTIVE"},
            {"이서연", "010-2345-6789", "F", "ACTIVE"},
            {"박지훈", "010-3456-7890", "M", "ACTIVE"},
            {"최수아", "010-4567-8901", "F", "ACTIVE"},
            {"정도윤", "010-5678-9012", "M", "EXPIRED"},
            {"강하은", "010-6789-0123", "F", "ACTIVE"},
            {"조현우", "010-7890-1234", "M", "SUSPENDED"},
            {"윤서진", "010-8901-2345", "F", "ACTIVE"},
            {"임준서", "010-9012-3456", "M", "ACTIVE"},
            {"한지아", "010-0123-4567", "F", "ACTIVE"},
        };
        for (String[] row : data) {
            Member m = new Member();
            m.setId(seq.getAndIncrement());
            m.setName(row[0]);
            m.setPhone(row[1]);
            m.setGender(row[2]);
            m.setStatus(row[3]);
            m.setJoinDate(LocalDate.now().minusMonths(new Random().nextInt(12)));
            m.setMembershipEnd(LocalDate.now().plusMonths(new Random().nextInt(6)));
            store.put(m.getId(), m);
        }
    }

    @Override
    public List<Member> findAll(String keyword, String status, int page, int size) {
        return store.values().stream()
            .filter(m -> keyword == null || keyword.isBlank() ||
                m.getName().contains(keyword) || m.getPhone().contains(keyword))
            .filter(m -> status == null || status.isBlank() || m.getStatus().equals(status))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String keyword, String status) {
        return store.values().stream()
            .filter(m -> keyword == null || keyword.isBlank() ||
                m.getName().contains(keyword) || m.getPhone().contains(keyword))
            .filter(m -> status == null || status.isBlank() || m.getStatus().equals(status))
            .count();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Member save(Member member) {
        member.setId(seq.getAndIncrement());
        member.setJoinDate(LocalDate.now());
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member update(Long id, Member member) {
        member.setId(id);
        store.put(id, member);
        return member;
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setStatus(status));
    }

    @Override
    public void freeze(Long id, String startDate, String endDate) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setStatus("SUSPENDED"));
    }
}
