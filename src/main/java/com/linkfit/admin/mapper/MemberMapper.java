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
                         @Param("tier") String tier, @Param("offset") int offset, @Param("size") int size);
    long count(@Param("keyword") String keyword, @Param("status") String status, @Param("tier") String tier);
    Optional<Member> findById(@Param("id") String id);
    void insertUser(Member member);
    void insertProfile(Member member);
    void update(Member member);
    void delete(@Param("id") String id);
    void updateStatus(@Param("id") String id, @Param("isActive") int isActive);
    void updateTier(@Param("id") String id, @Param("tier") String tier);
    void updateMemberType(@Param("id") String id, @Param("memberType") String memberType);
    void updateRole(@Param("id") String id, @Param("role") String role);
    void insertFreeze(@Param("memberId") String memberId, @Param("freezeStart") String freezeStart,
                      @Param("freezeEnd") String freezeEnd, @Param("reason") String reason);
    List<MemberFreeze> findFreezeByMemberId(@Param("memberId") String memberId);
    List<Membership> findMembershipsByMemberId(@Param("memberId") String memberId);
    void insertMembership(Membership membership);

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
