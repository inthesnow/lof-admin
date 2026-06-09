package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.CrmDailyStats;
import com.linkfit.admin.mapper.CrmDailyStatsMapper;
import com.linkfit.admin.scheduler.DailyStatsScheduler;
import com.linkfit.admin.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    private static final Logger log = LoggerFactory.getLogger(StatsApiController.class);

    private final CrmDailyStatsMapper dailyStatsMapper;
    private final DailyStatsScheduler scheduler;

    public StatsApiController(CrmDailyStatsMapper dailyStatsMapper, DailyStatsScheduler scheduler) {
        this.dailyStatsMapper = dailyStatsMapper;
        this.scheduler = scheduler;
    }

    @GetMapping("/daily")
    public ApiResponse<List<CrmDailyStats>> dailyStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Stats] GET /api/stats/daily - startDate={}, endDate={}", startDate, endDate);
        Long gymId = (principal != null) ? principal.getGymId() : 1L;
        String start = (startDate != null && !startDate.isEmpty()) ? startDate
                : LocalDate.now().minusDays(29).toString();
        String end = (endDate != null && !endDate.isEmpty()) ? endDate
                : LocalDate.now().toString();
        return ApiResponse.ok(dailyStatsMapper.findRecent(gymId, start, end));
    }

    @PostMapping("/daily/aggregate")
    public ApiResponse<Void> triggerAggregate(@AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Stats] POST /api/stats/daily/aggregate");
        Long gymId = (principal != null) ? principal.getGymId() : 1L;
        scheduler.aggregateToday(gymId);
        return ApiResponse.ok();
    }
}
