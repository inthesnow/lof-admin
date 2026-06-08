package com.linkfit.admin.service;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
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
    void freeze(String id, String startDate, String endDate);
    List<MemberTicket> findTickets(String id);
    void chargeTicket(String id, String ticketType, int amount, String description);
}
