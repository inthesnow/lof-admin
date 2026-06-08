package com.linkfit.admin;

import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.mapper.CrmDailyStatsMapper;
import com.linkfit.admin.mapper.FeedbackTicketMapper;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.scheduler.DailyStatsScheduler;
import com.linkfit.admin.service.ReRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyStatsSchedulerTest {

    @Mock CrmDailyStatsMapper dailyStatsMapper;
    @Mock FeedbackTicketMapper feedbackTicketMapper;
    @Mock MemberMapper memberMapper;
    @Mock ReRegistrationService reRegistrationService;

    DailyStatsScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new DailyStatsScheduler(
                dailyStatsMapper, feedbackTicketMapper, memberMapper, reRegistrationService);
    }

    @Test
    void expireOverdueTickets_callsMapper() {
        when(feedbackTicketMapper.expireOverdue()).thenReturn(3);
        scheduler.expireOverdueTickets();
        verify(feedbackTicketMapper, times(1)).expireOverdue();
    }

    @Test
    void expireOverdueTickets_noExpiredTickets_stillCallsMapper() {
        when(feedbackTicketMapper.expireOverdue()).thenReturn(0);
        scheduler.expireOverdueTickets();
        verify(feedbackTicketMapper, times(1)).expireOverdue();
    }

    @Test
    void aggregateDailyStats_noGyms_skips() {
        when(dailyStatsMapper.findAllGymIds()).thenReturn(Collections.emptyList());
        scheduler.aggregateDailyStats();
        verify(dailyStatsMapper, never()).aggregate(any(), any());
    }

    @Test
    void aggregateDailyStats_multipleGyms_aggregatesAll() {
        List<Long> gymIds = List.of(1L, 2L, 3L);
        when(dailyStatsMapper.findAllGymIds()).thenReturn(gymIds);
        String yesterday = LocalDate.now().minusDays(1).toString();

        scheduler.aggregateDailyStats();

        for (Long gymId : gymIds) {
            verify(dailyStatsMapper).aggregate(gymId, yesterday);
        }
    }

    @Test
    void issueMonthlyTickets_memberHasNoTicket_insertsTicket() {
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        when(dailyStatsMapper.findAllGymIds()).thenReturn(List.of(1L));
        when(memberMapper.findAllActiveIds()).thenReturn(List.of("member-001"));
        when(feedbackTicketMapper.existsForMonth("member-001", 1L, monthYear)).thenReturn(false);

        scheduler.issueMonthlyTickets();

        ArgumentCaptor<FeedbackTicket> captor = ArgumentCaptor.forClass(FeedbackTicket.class);
        verify(feedbackTicketMapper).insert(captor.capture());
        FeedbackTicket issued = captor.getValue();
        assertThat(issued.getMemberId()).isEqualTo("member-001");
        assertThat(issued.getGymId()).isEqualTo(1L);
        assertThat(issued.getTicketType()).isEqualTo("free");
        assertThat(issued.getStatus()).isEqualTo("issued");
        assertThat(issued.getMonthYear()).isEqualTo(monthYear);
    }

    @Test
    void issueMonthlyTickets_memberAlreadyHasTicket_skipsInsert() {
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        when(dailyStatsMapper.findAllGymIds()).thenReturn(List.of(1L));
        when(memberMapper.findAllActiveIds()).thenReturn(List.of("member-001"));
        when(feedbackTicketMapper.existsForMonth("member-001", 1L, monthYear)).thenReturn(true);

        scheduler.issueMonthlyTickets();

        verify(feedbackTicketMapper, never()).insert(any());
    }

    @Test
    void issueMonthlyTickets_noActiveMembers_insertsNothing() {
        when(dailyStatsMapper.findAllGymIds()).thenReturn(List.of(1L));
        when(memberMapper.findAllActiveIds()).thenReturn(Collections.emptyList());

        scheduler.issueMonthlyTickets();

        verify(feedbackTicketMapper, never()).insert(any());
    }

    @Test
    void autoClassifyReRegistration_callsServicePerGym() {
        when(dailyStatsMapper.findAllGymIds()).thenReturn(List.of(1L, 2L));
        when(reRegistrationService.autoClassify(anyLong())).thenReturn(0);

        scheduler.autoClassifyReRegistration();

        verify(reRegistrationService).autoClassify(1L);
        verify(reRegistrationService).autoClassify(2L);
    }
}
