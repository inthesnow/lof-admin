package com.linkfit.admin.service;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<Member> findAll(String keyword, String status, String tier, Long gymId, List<String> trainerIds,
                          Integer minDaysLeft, Integer maxDaysLeft, Integer minPtRemaining, Integer minAbsentDays,
                          int page, int size);
    long count(String keyword, String status, String tier, Long gymId, List<String> trainerIds,
               Integer minDaysLeft, Integer maxDaysLeft, Integer minPtRemaining, Integer minAbsentDays);
    Optional<Member> findById(String id, Long gymId);
    Member save(Member member, Long gymId);
    Member update(String id, Member member, Long gymId);
    void delete(String id, Long gymId);
    void updateStatus(String id, String status, Long gymId);
    void updateTier(String id, String tier, Long gymId);
    void updateMemberType(String id, String memberType, Long gymId);
    void updateRole(String id, String role, Long gymId);
    void updateAssignedTrainer(String id, String trainerId, Long gymId);
    void freeze(String id, String startDate, String endDate, Long gymId);
    void withdraw(String id, Long gymId);
    List<Membership> findMemberships(String id);
    void addMembership(Membership membership);
    List<MemberTicket> findTickets(String id);
    void chargeTicket(String id, String ticketType, int amount, String description);
}
