package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.MemberFilter;
import com.linkfit.admin.domain.MemberFreeze;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.domain.PtMember;
import com.linkfit.admin.domain.TicketLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    List<Member> findAll(@Param("f") MemberFilter filter, @Param("gymId") Long gymId,
                         @Param("orderBy") String orderByClause,
                         @Param("offset") int offset, @Param("size") int size);
    long count(@Param("f") MemberFilter filter, @Param("gymId") Long gymId);
    Optional<Member> findById(@Param("id") String id, @Param("gymId") Long gymId);
    boolean existsInGym(@Param("id") String id, @Param("gymId") Long gymId);
    boolean existsByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
    Optional<String> findIdByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
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

    // trainer_members 동기화용 (updateAssignedTrainer가 user_profiles만 갱신하고 이 테이블은
    // 건드리지 않아 원포인트 요청/트레이너 앱 담당회원 목록이 옛 트레이너 기준으로 남던 문제 수정,
    // 2026-09-08). lof-backend TrainerService.assignTrainerByMember와 동일한 패턴.
    String findTrainerMemberType(@Param("memberId") String memberId);
    void deleteTrainerMembersByMember(@Param("memberId") String memberId);
    void insertTrainerMember(@Param("trainerId") String trainerId, @Param("memberId") String memberId,
                              @Param("memberType") String memberType);

    // PT 세션을 한 번도 안 가져본 회원이 트레이너에 먼저 배정되면 trainer_members.member_type이
    // ONE_POINT로 기본값 저장되는데, 그 뒤에 PT를 등록해도 이 컬럼은 따로 안 건드려서 트레이너
    // 앱에서는 계속 "원포인트 회원"으로 보이는 버그가 있었음(2026-11-01) — 트레이너 최초 배정 시
    // 기본값을 정할 때도 실제 PT 보유 여부를 확인하도록, PT를 지급하는 시점에도 이미 배정된
    // trainer_members 행을 PT로 동기화하도록 아래 2개를 추가.
    boolean hasPtSessions(@Param("memberId") String memberId);
    void markPtMemberType(@Param("memberId") String memberId);

    // CRM에서 구독 등급을 "부여"할 때 그 등급의 월 지급량대로 티켓을 채우기 위함(2026-11-10).
    // 지급량은 코드에 또 하드코딩하지 않고 subscription_plans 테이블을 읽는다 — 같은 값이
    // IapVerifyService/linkonfit PaymentService에 이미 중복 하드코딩돼 있어 4번째 복사본을
    // 만들지 않기 위함(값은 전수 대조해 일치 확인함).
    Map<String, Object> findPlanTickets(@Param("planCode") String planCode);

    // 등급 지급은 "이번 달 한도"를 세팅하는 개념이라 증분(upsertTicket)이 아니라 절대값으로
    // 덮어쓴다(IAP의 setSubscriptionTickets와 동일 의미). 단품 유료 구매분(paid_remaining)은
    // 정산 원장과 연결된 별도 컬럼이라 절대 건드리지 않는다.
    void setTicketAbsolute(@Param("userId") String userId, @Param("ticketType") String ticketType,
                            @Param("amount") int amount, @Param("gymId") Long gymId);
    int withdraw(@Param("id") String id, @Param("gymId") Long gymId);
    void insertFreeze(@Param("memberId") String memberId, @Param("freezeStart") String freezeStart,
                      @Param("freezeEnd") String freezeEnd, @Param("reason") String reason);
    List<MemberFreeze> findFreezeByMemberId(@Param("memberId") String memberId);
    List<Membership> findMembershipsByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    Optional<Membership> findMembershipById(@Param("id") Long id, @Param("gymId") Long gymId);
    void insertMembership(@Param("m") Membership membership, @Param("gymId") Long gymId);
    void updateMembershipEndDate(@Param("id") Long id, @Param("endDate") String endDate, @Param("gymId") Long gymId);
    void updateMembershipAmounts(@Param("id") Long id, @Param("price") int price,
                                  @Param("discountAmount") int discountAmount, @Param("paidAmount") int paidAmount,
                                  @Param("paymentMethod") String paymentMethod, @Param("regType") String regType,
                                  @Param("gymId") Long gymId);
    void deleteMembership(@Param("id") Long id, @Param("gymId") Long gymId);
    void deleteMembershipsByMemberAndPackage(@Param("memberId") String memberId, @Param("packageId") Long packageId,
                                              @Param("gymId") Long gymId);
    // 양도: 원 회원의 행을 삭제 대신 상태만 변경(이력 보존)
    void markMembershipTransferred(@Param("id") Long id, @Param("gymId") Long gymId);
    // 신규/재유입/재등록 분류용 — 이 회원의 과거 이용권/PT 이력 요약({cnt, lastEndDate})
    Map<String, Object> findMembershipHistorySummary(@Param("memberId") String memberId);

    List<Membership> findExpiringMemberships(@Param("days") int days, @Param("gymId") Long gymId,
                                              @Param("offset") int offset, @Param("size") int size);
    long countExpiringMemberships(@Param("days") int days, @Param("gymId") Long gymId);

    // 회원관리 페이지 상단 집계 패널 — {total, valid, expired, expiring}
    Map<String, Object> summaryCounts(@Param("gymId") Long gymId);

    List<MemberTicket> findTickets(@Param("userId") String userId);
    Integer findTicketRemaining(@Param("userId") String userId, @Param("ticketType") String ticketType);
    // 활성 회원 전체(ONE_POINT/FEEDBACK/PHOTO/VIDEO) 잔량 합계 — {onePoint, feedback, photo, video}
    Map<String, Object> ticketTotals(@Param("gymId") Long gymId);
    void upsertTicket(@Param("userId") String userId, @Param("ticketType") String ticketType,
                      @Param("amount") int amount, @Param("gymId") Long gymId);
    void insertTicketLog(@Param("userId") String userId, @Param("ticketType") String ticketType,
                         @Param("actionType") String actionType, @Param("description") String description);

    // 티켓 지급/차감 사용내역 (구독권/티켓 관리 > 사용내역 탭)
    List<TicketLog> findTicketLogs(@Param("gymId") Long gymId, @Param("ticketType") String ticketType,
                                    @Param("keyword") String keyword,
                                    @Param("offset") int offset, @Param("size") int size);
    long countTicketLogs(@Param("gymId") Long gymId, @Param("ticketType") String ticketType,
                         @Param("keyword") String keyword);

    List<String> findAllActiveIds();

    List<PtMember> findPtMembers(@Param("lowStock") boolean lowStock,
                                  @Param("offset") int offset, @Param("size") int size,
                                  @Param("gymId") Long gymId);
    long countPtMembers(@Param("lowStock") boolean lowStock, @Param("gymId") Long gymId);

    // 실제 PT 세션 조정 — 구매분(pt_sessions_left)과 서비스/특별지급분(service_pt_sessions_left)을 분리 관리
    void adjustPtSessions(@Param("memberId") String memberId, @Param("delta") int delta, @Param("gymId") Long gymId);
    void adjustServicePtSessions(@Param("memberId") String memberId, @Param("delta") int delta, @Param("gymId") Long gymId);
}
