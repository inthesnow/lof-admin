package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.service.MemberService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile("dev")
public class MyBatisMemberService implements MemberService {

    private final MemberMapper memberMapper;

    public MyBatisMemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public List<Member> findAll(String keyword, String status, String tier, int page, int size) {
        return memberMapper.findAll(keyword, status, tier, page * size, size);
    }

    @Override
    public long count(String keyword, String status, String tier) {
        return memberMapper.count(keyword, status, tier);
    }

    @Override
    public Optional<Member> findById(String id) {
        return memberMapper.findById(id);
    }

    @Override
    public Member save(Member member) {
        if (member.getId() == null || member.getId().isBlank()) {
            member.setId(UUID.randomUUID().toString());
        }
        memberMapper.insertUser(member);
        memberMapper.insertProfile(member);
        return member;
    }

    @Override
    public Member update(String id, Member member) {
        member.setId(id);
        memberMapper.update(member);
        return member;
    }

    @Override
    public void delete(String id) {
        memberMapper.delete(id);
    }

    @Override
    public void updateStatus(String id, String status) {
        memberMapper.updateStatus(id, "ACTIVE".equals(status) ? 1 : 0);
    }

    @Override
    public void updateTier(String id, String tier) {
        memberMapper.updateTier(id, tier);
    }

    @Override
    public void updateMemberType(String id, String memberType) {
        memberMapper.updateMemberType(id, memberType);
    }

    @Override
    public void updateRole(String id, String role) {
        memberMapper.updateRole(id, role);
    }

    @Override
    public void freeze(String id, String startDate, String endDate) {
        memberMapper.insertFreeze(id, startDate, endDate, null);
        memberMapper.updateStatus(id, 0);
    }

    @Override
    public void withdraw(String id) {
        memberMapper.withdraw(id);
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
