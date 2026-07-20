package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberFreeze;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.domain.PtMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    List<Member> findAll(@Param("keyword") String keyword, @Param("status") String status,
                         @Param("tier") String tier, @Param("gymId") Long gymId,
                         @Param("trainerIds") List<String> trainerIds,
                         @Param("minDaysLeft") Integer minDaysLeft, @Param("maxDaysLeft") Integer maxDaysLeft,
                         @Param("minPtRemaining") Integer minPtRemaining, @Param("minAbsentDays") Integer minAbsentDays,
                         @Param("offset") int offset, @Param("size") int size);
    long count(@Param("keyword") String keyword, @Param("status") String status,
               @Param("tier") String tier, @Param("gymId") Long gymId,
               @Param("trainerIds") List<String> trainerIds,
               @Param("minDaysLeft") Integer minDaysLeft, @Param("maxDaysLeft") Integer maxDaysLeft,
               @Param("minPtRemaining") Integer minPtRemaining, @Param("minAbsentDays") Integer minAbsentDays);
    Optional<Member> findById(@Param("id") String id, @Param("gymId") Long gymId);
    boolean existsInGym(@Param("id") String id, @Param("gymId") Long gymId);
    void insertUser(Member member);
    void insertProfile(Member member);
    void insertUserGym(@Param("userId") String userId, @Param("gymId") Long gymId);
    int update(@Param("member") Member member, @Param("gymId") Long gymId);
    int delete(@Param("id") String id, @Param("gymId") Long gymId);
    int updateStatus(@Param("id") String id, @Param("isActive") int isActive, @Param("gymId") Long gymId);
    int updateTier(@Param("id") String id, @Param("tier") String tier, @Param("gymId") Long gymId);
    int updateMemberType(@Param("id") String id, @Param("memberType") String memberType, @Param("gymId") Long gymId);
    int updateRole(@Param("id") String id, @Param("role") String role, @Param("gymId") Long gymId);
    int updateAssignedTrainer(@Param("id") String id, @Param("trainerId") String trainerId, @Param("gymId") Long gymId);
    int withdraw(@Param("id") String id, @Param("gymId") Long gymId);
    void insertFreeze(@Param("memberId") String memberId, @Param("freezeStart") String freezeStart,
                      @Param("freezeEnd") String freezeEnd, @Param("reason") String reason);
    List<MemberFreeze> findFreezeByMemberId(@Param("memberId") String memberId);
    List<Membership> findMembershipsByMemberId(@Param("memberId") String memberId);
    void insertMembership(Membership membership);
    void updateMembershipEndDate(@Param("id") Long id, @Param("endDate") String endDate);
    void deleteMembership(@Param("id") Long id);

    List<Membership> findExpiringMemberships(@Param("days") int days,
                                              @Param("offset") int offset, @Param("size") int size);
    long countExpiringMemberships(@Param("days") int days);

    List<MemberTicket> findTickets(@Param("userId") String userId);
    void upsertTicket(@Param("userId") String userId, @Param("ticketType") String ticketType,
                      @Param("amount") int amount);
    void insertTicketLog(@Param("userId") String userId, @Param("ticketType") String ticketType,
                         @Param("actionType") String actionType, @Param("description") String description);

    List<String> findAllActiveIds();

    List<PtMember> findPtMembers(@Param("lowStock") boolean lowStock,
                                  @Param("offset") int offset, @Param("size") int size);
    long countPtMembers(@Param("lowStock") boolean lowStock);
}
