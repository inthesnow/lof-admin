package com.linkfit.admin.service;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<Member> findAll(String keyword, String status, String tier, int page, int size);
    long count(String keyword, String status, String tier);
    Optional<Member> findById(String id);
    Member save(Member member);
    Member update(String id, Member member);
    void delete(String id);
    void updateStatus(String id, String status);
    void updateTier(String id, String tier);
    void updateMemberType(String id, String memberType);
    void updateRole(String id, String role);
    void freeze(String id, String startDate, String endDate);
    void withdraw(String id);
    List<Membership> findMemberships(String id);
    void addMembership(Membership membership);
    List<MemberTicket> findTickets(String id);
    void chargeTicket(String id, String ticketType, int amount, String description);
}
