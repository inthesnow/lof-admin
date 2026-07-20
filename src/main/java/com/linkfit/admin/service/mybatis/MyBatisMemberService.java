package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyBatisMemberService implements MemberService {

    private final MemberMapper memberMapper;

    public MyBatisMemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public List<Member> findAll(String keyword, String status, String tier, Long gymId, List<String> trainerIds,
                                 Integer minDaysLeft, Integer maxDaysLeft, Integer minPtRemaining, Integer minAbsentDays,
                                 int page, int size) {
        return memberMapper.findAll(keyword, status, tier, gymId, trainerIds,
                minDaysLeft, maxDaysLeft, minPtRemaining, minAbsentDays, page * size, size);
    }

    @Override
    public long count(String keyword, String status, String tier, Long gymId, List<String> trainerIds,
                       Integer minDaysLeft, Integer maxDaysLeft, Integer minPtRemaining, Integer minAbsentDays) {
        return memberMapper.count(keyword, status, tier, gymId, trainerIds,
                minDaysLeft, maxDaysLeft, minPtRemaining, minAbsentDays);
    }

    @Override
    public Optional<Member> findById(String id, Long gymId) {
        return memberMapper.findById(id, gymId);
    }

    @Override
    public Member save(Member member, Long gymId) {
        if (member.getId() == null || member.getId().isBlank()) {
            member.setId(UUID.randomUUID().toString());
        }
        memberMapper.insertUser(member);
        memberMapper.insertProfile(member);
        if (gymId != null) {
            memberMapper.insertUserGym(member.getId(), gymId);
        }
        return member;
    }

    @Override
    public Member update(String id, Member member, Long gymId) {
        member.setId(id);
        memberMapper.update(member, gymId);
        return member;
    }

    @Override
    public void delete(String id, Long gymId) {
        memberMapper.delete(id, gymId);
    }

    @Override
    public void updateStatus(String id, String status, Long gymId) {
        memberMapper.updateStatus(id, "ACTIVE".equals(status) ? 1 : 0, gymId);
    }

    @Override
    public void updateTier(String id, String tier, Long gymId) {
        memberMapper.updateTier(id, tier, gymId);
    }

    @Override
    public void updateMemberType(String id, String memberType, Long gymId) {
        memberMapper.updateMemberType(id, memberType, gymId);
    }

    @Override
    public void updateRole(String id, String role, Long gymId) {
        memberMapper.updateRole(id, role, gymId);
    }

    @Override
    public void updateAssignedTrainer(String id, String trainerId, Long gymId) {
        memberMapper.updateAssignedTrainer(id, trainerId, gymId);
    }

    @Override
    public void freeze(String id, String startDate, String endDate, Long gymId) {
        memberMapper.insertFreeze(id, startDate, endDate, null);
        memberMapper.updateStatus(id, 0, gymId);
    }

    @Override
    public void withdraw(String id, Long gymId) {
        memberMapper.withdraw(id, gymId);
    }

    @Override
    public List<Membership> findMemberships(String id) {
        return memberMapper.findMembershipsByMemberId(id);
    }

    @Override
    public void addMembership(Membership membership) {
        memberMapper.insertMembership(membership);
    }

    @Override
    public List<MemberTicket> findTickets(String id) {
        return memberMapper.findTickets(id);
    }

    @Override
    public void chargeTicket(String id, String ticketType, int amount, String description) {
        memberMapper.upsertTicket(id, ticketType, amount);
        String actionType = amount >= 0 ? "CHARGE" : "USE";
        String desc = (description != null && !description.isBlank()) ? description : (amount >= 0 ? "관리자 지급" : "관리자 차감");
        memberMapper.insertTicketLog(id, ticketType, actionType, desc);
    }
}
