package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.service.MemberService;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MockMemberService implements MemberService {

    private final Map<String, Member> store = new LinkedHashMap<>();

    public MockMemberService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"김민준", "010-1234-5678", "M", "ACTIVE"},
            {"이서연", "010-2345-6789", "F", "ACTIVE"},
            {"박지훈", "010-3456-7890", "M", "ACTIVE"},
            {"최수아", "010-4567-8901", "F", "ACTIVE"},
            {"강하은", "010-6789-0123", "F", "ACTIVE"},
        };
        for (String[] row : data) {
            Member m = new Member();
            m.setId(UUID.randomUUID().toString());
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
    public List<Member> findAll(String keyword, String status, String tier, int page, int size) {
        return store.values().stream()
            .filter(m -> keyword == null || keyword.isBlank() ||
                m.getName().contains(keyword) || m.getPhone().contains(keyword))
            .filter(m -> status == null || status.isBlank() || m.getStatus().equals(status))
            .filter(m -> tier == null || tier.isBlank() || tier.equals(m.getTier()))
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count(String keyword, String status, String tier) {
        return store.values().stream()
            .filter(m -> keyword == null || keyword.isBlank() ||
                m.getName().contains(keyword) || m.getPhone().contains(keyword))
            .filter(m -> status == null || status.isBlank() || m.getStatus().equals(status))
            .filter(m -> tier == null || tier.isBlank() || tier.equals(m.getTier()))
            .count();
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Member save(Member member) {
        if (member.getId() == null || member.getId().isBlank()) {
            member.setId(UUID.randomUUID().toString());
        }
        member.setJoinDate(LocalDate.now());
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member update(String id, Member member) {
        member.setId(id);
        store.put(id, member);
        return member;
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }

    @Override
    public void updateStatus(String id, String status) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setStatus(status));
    }

    @Override
    public void updateTier(String id, String tier) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setTier(tier));
    }

    @Override
    public void updateMemberType(String id, String memberType) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setMemberType(memberType));
    }

    @Override
    public void freeze(String id, String startDate, String endDate) {
        Optional.ofNullable(store.get(id)).ifPresent(m -> m.setStatus("SUSPENDED"));
    }

    @Override
    public List<MemberTicket> findTickets(String id) { return Collections.emptyList(); }

    @Override
    public void chargeTicket(String id, String ticketType, int amount, String description) {}
}
