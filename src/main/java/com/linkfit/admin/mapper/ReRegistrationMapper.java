package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.CrmReregistrationNote;
import com.linkfit.admin.domain.ReRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface ReRegistrationMapper {

    List<ReRegistration> findAll(@Param("gymId") Long gymId, @Param("status") String status,
                                  @Param("reason") String reason,
                                  @Param("expiryStatus") String expiryStatus,
                                  @Param("startDate") String startDate, @Param("endDate") String endDate,
                                  @Param("expiryMonth") String expiryMonth,
                                  @Param("offset") int offset, @Param("size") int size);
    long count(@Param("gymId") Long gymId, @Param("status") String status, @Param("reason") String reason,
               @Param("expiryStatus") String expiryStatus,
               @Param("startDate") String startDate, @Param("endDate") String endDate,
               @Param("expiryMonth") String expiryMonth);

    // 대상자/예정자/만료 — 실제 회원권 만료일(membership.end_date) 기준 집계.
    // 목록과 동일한 필터를 받아 요약 숫자가 화면과 어긋나지 않게 한다(2026-11-15).
    Map<String, Object> summaryByMembership(@Param("gymId") Long gymId, @Param("status") String status,
                                            @Param("reason") String reason,
                                            @Param("expiryStatus") String expiryStatus,
                                            @Param("startDate") String startDate, @Param("endDate") String endDate,
                                            @Param("expiryMonth") String expiryMonth);

    /** 만료월 드롭다운 — 실제 데이터가 있는 'YYYY-MM' 목록 */
    List<String> findExpiryMonths(@Param("gymId") Long gymId);

    Optional<ReRegistration> findById(@Param("id") String id, @Param("gymId") Long gymId);

    void insert(ReRegistration r);
    void assign(@Param("id") String id, @Param("assignedTo") String assignedTo, @Param("gymId") Long gymId);

    // 자동 분류: 이미 존재하는 항목인지 확인 (중복 방지)
    boolean existsByMemberAndReason(@Param("memberId") String memberId,
                                     @Param("gymId") Long gymId,
                                     @Param("reason") String reason);

    // 집계
    int countByStatus(@Param("gymId") Long gymId, @Param("status") String status);
    int countByStatusInPeriod(@Param("gymId") Long gymId, @Param("status") String status,
                               @Param("days") int days);

    // ── 메모(스택형, crm_member_notes와 동일한 패턴) ──────────────────
    List<CrmReregistrationNote> findNotesByReregistrationId(@Param("reregistrationId") String reregistrationId,
                                                             @Param("gymId") Long gymId);
    void insertNote(CrmReregistrationNote note);
}
