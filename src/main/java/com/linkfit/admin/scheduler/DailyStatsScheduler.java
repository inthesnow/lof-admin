package com.linkfit.admin.scheduler;

import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.mapper.CrmDailyStatsMapper;
import com.linkfit.admin.mapper.FeedbackTicketMapper;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.service.ReRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class DailyStatsScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyStatsScheduler.class);

    private final CrmDailyStatsMapper dailyStatsMapper;
    private final FeedbackTicketMapper feedbackTicketMapper;
    private final MemberMapper memberMapper;
    private final ReRegistrationService reRegistrationService;

    public DailyStatsScheduler(CrmDailyStatsMapper dailyStatsMapper,
                                 FeedbackTicketMapper feedbackTicketMapper,
                                 MemberMapper memberMapper,
                                 ReRegistrationService reRegistrationService) {
        this.dailyStatsMapper = dailyStatsMapper;
        this.feedbackTicketMapper = feedbackTicketMapper;
        this.memberMapper = memberMapper;
        this.reRegistrationService = reRegistrationService;
    }

    /** 매일 01:00 — 전날 통계 집계 */
    @Scheduled(cron = "0 0 1 * * *")
    public void aggregateDailyStats() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        List<Long> gymIds = dailyStatsMapper.findAllGymIds();
        if (gymIds.isEmpty()) { log.info("[Stats] 등록된 gym 없음, 건너뜀"); return; }
        for (Long gymId : gymIds) {
            try {
                dailyStatsMapper.aggregate(gymId, yesterday);
                log.info("[Stats] gymId={} date={} 집계 완료", gymId, yesterday);
            } catch (Exception e) {
                log.error("[Stats] gymId={} 집계 실패: {}", gymId, e.getMessage());
            }
        }
    }

    /** 매일 00:05 — 만료 티켓 상태 업데이트 */
    @Scheduled(cron = "0 5 0 * * *")
    public void expireOverdueTickets() {
        int count = feedbackTicketMapper.expireOverdue();
        if (count > 0) log.info("[Ticket] 만료 처리 {}건", count);
    }

    /** 매달 1일 00:10 — 월초 무료 피드백 티켓 자동 발급 */
    @Scheduled(cron = "0 10 0 1 * *")
    public void issueMonthlyTickets() {
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Long> gymIds = dailyStatsMapper.findAllGymIds();
        List<String> memberIds = memberMapper.findAllActiveIds();

        for (Long gymId : gymIds) {
            int issued = 0;
            for (String memberId : memberIds) {
                try {
                    if (!feedbackTicketMapper.existsForMonth(memberId, gymId, monthYear)) {
                        FeedbackTicket ticket = buildFreeTicket(memberId, gymId, monthYear);
                        feedbackTicketMapper.insert(ticket);
                        issued++;
                    }
                } catch (Exception e) {
                    log.warn("[Ticket] 발급 실패 memberId={} gymId={}: {}", memberId, gymId, e.getMessage());
                }
            }
            if (issued > 0) log.info("[Ticket] gymId={} 월초 발급 {}명", gymId, issued);
        }
    }

    /** 매일 06:00 — 재등록 자동 분류 */
    @Scheduled(cron = "0 0 6 * * *")
    public void autoClassifyReRegistration() {
        List<Long> gymIds = dailyStatsMapper.findAllGymIds();
        for (Long gymId : gymIds) {
            try {
                int created = reRegistrationService.autoClassify(gymId);
                if (created > 0) log.info("[ReReg] gymId={} 자동 분류 {}건", gymId, created);
            } catch (Exception e) {
                log.error("[ReReg] gymId={} 자동 분류 실패: {}", gymId, e.getMessage());
            }
        }
    }

    /** 수동 트리거 — 오늘 날짜 기준 통계 즉시 집계 */
    public void aggregateToday(Long gymId) {
        String today = LocalDate.now().toString();
        dailyStatsMapper.aggregate(gymId, today);
        log.info("[Stats] 수동 집계 gymId={} date={}", gymId, today);
    }

    private FeedbackTicket buildFreeTicket(String memberId, Long gymId, String monthYear) {
        LocalDate first = LocalDate.parse(monthYear + "-01");
        LocalDateTime expires = first.withDayOfMonth(first.lengthOfMonth()).atTime(23, 59, 59);
        FeedbackTicket t = new FeedbackTicket();
        t.setId(UUID.randomUUID().toString());
        t.setMemberId(memberId);
        t.setGymId(gymId);
        t.setTicketType("free");
        t.setStatus("issued");
        t.setMonthYear(monthYear);
        t.setExpiresAt(expires);
        return t;
    }
}
