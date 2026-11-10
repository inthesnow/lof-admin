package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.CrmReregistrationNote;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.domain.ReRegistration;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.mapper.ReRegistrationMapper;
import com.linkfit.admin.service.ReRegistrationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyBatisReRegistrationService implements ReRegistrationService {

    private final ReRegistrationMapper mapper;
    private final MemberMapper memberMapper;

    public MyBatisReRegistrationService(ReRegistrationMapper mapper, MemberMapper memberMapper) {
        this.mapper       = mapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public List<ReRegistration> findAll(Long gymId, String status, String reason,
                                         String expiryStatus, String startDate, String endDate, int page, int size) {
        return mapper.findAll(gymId, status, reason, expiryStatus, startDate, endDate, page * size, size);
    }

    @Override
    public long count(Long gymId, String status, String reason, String expiryStatus, String startDate, String endDate) {
        return mapper.count(gymId, status, reason, expiryStatus, startDate, endDate);
    }

    @Override
    public Map<String, Object> membershipSummary(Long gymId) {
        // summaryByMembership은 GROUP BY 없는 SUM 집계라 대상 행이 0건이면 전 컬럼이 NULL인
        // 행 1개가 나오고, MyBatis는 그런 행을 Map이 아니라 null로 매핑한다 — 그래서 재등록
        // 데이터가 아직 없는 지점(신규 지점 또는 06:00 자동분류 배치 실행 전)에서 이 요약 카드가
        // NPE로 500을 내고 있었다(2026-11-10 전수 점검에서 발견).
        Map<String, Object> raw = mapper.summaryByMembership(gymId);
        if (raw == null) raw = Map.of();
        int expiring = toInt(raw.get("expiring"));
        int expired  = toInt(raw.get("expired"));
        return Map.of(
                "target",   expiring + expired,
                "expiring", expiring,
                "expired",  expired
        );
    }

    private int toInt(Object val) {
        return (val instanceof Number n) ? n.intValue() : 0;
    }

    @Override
    public Optional<ReRegistration> findById(String id, Long gymId) {
        return mapper.findById(id, gymId);
    }

    @Override
    public void assign(String id, String assignedTo, Long gymId) {
        mapper.assign(id, assignedTo, gymId);
    }

    @Override
    public List<CrmReregistrationNote> findNotes(String reregistrationId, Long gymId) {
        return mapper.findNotesByReregistrationId(reregistrationId, gymId);
    }

    @Override
    public CrmReregistrationNote addNote(String reregistrationId, Long gymId, String authorId, String content) {
        CrmReregistrationNote note = new CrmReregistrationNote();
        note.setId(UUID.randomUUID().toString());
        note.setReregistrationId(reregistrationId);
        note.setGymId(gymId);
        note.setAuthorId(authorId);
        note.setContent(content);
        mapper.insertNote(note);
        return note;
    }

    @Override
    public int autoClassify(Long gymId) {
        int created = 0;

        // 이용권 만료 30일 이내 (이 gym 소속 회원만 — 배치 작업이라 페이지네이션 없이 넉넉한 상한만 둔다)
        List<Membership> expiring = memberMapper.findExpiringMemberships(30, gymId, 0, 10000);
        for (Membership m : expiring) {
            String memberId = m.getMemberId();
            if (memberId == null) continue;
            if (!mapper.existsByMemberAndReason(memberId, gymId, "membership_expiry")) {
                mapper.insert(buildRecord(memberId, gymId, "membership_expiry"));
                created++;
            }
        }

        return created;
    }

    @Override
    public Map<String, Integer> statusSummary(Long gymId) {
        return Map.of(
                "pending",     mapper.countByStatus(gymId, "pending"),
                "in_progress", mapper.countByStatus(gymId, "in_progress"),
                "success",     mapper.countByStatus(gymId, "success"),
                "failed",      mapper.countByStatus(gymId, "failed"),
                "hold",        mapper.countByStatus(gymId, "hold")
        );
    }

    private ReRegistration buildRecord(String memberId, Long gymId, String reason) {
        ReRegistration r = new ReRegistration();
        r.setId(UUID.randomUUID().toString());
        r.setMemberId(memberId);
        r.setGymId(gymId);
        r.setReason(reason);
        r.setStatus("pending");
        return r;
    }
}
