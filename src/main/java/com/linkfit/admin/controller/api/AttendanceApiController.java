package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.mapper.AttendanceMapper;
import com.linkfit.admin.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceApiController.class);

    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;

    public AttendanceApiController(AttendanceService attendanceService, AttendanceMapper attendanceMapper) {
        this.attendanceService = attendanceService;
        this.attendanceMapper  = attendanceMapper;
    }

    @GetMapping
    public ApiResponse<List<Attendance>> list(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        log.info("[Attendance] GET /api/attendance - date={}, period={}", date, period);
        return ApiResponse.ok(attendanceService.findAll(date, period));
    }

    @PostMapping
    public ApiResponse<Attendance> checkIn(@RequestBody Attendance attendance) {
        log.info("[Attendance] POST /api/attendance");
        return ApiResponse.ok(attendanceService.checkIn(attendance));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        log.info("[Attendance] DELETE /api/attendance/{id} - id={}", id);
        attendanceService.cancel(id);
        return ApiResponse.ok();
    }

    @GetMapping("/freeze")
    public ApiResponse<List<Attendance>> frozen(@RequestParam(defaultValue = "") String date) {
        log.info("[Attendance] GET /api/attendance/freeze - date={}", date);
        return ApiResponse.ok(attendanceService.findFrozen(date));
    }

    @GetMapping("/trend")
    public ApiResponse<List<Map<String, Object>>> trend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("[Attendance] GET /api/attendance/trend - startDate={}, endDate={}", startDate, endDate);
        String end   = (endDate   != null && !endDate.isEmpty())   ? endDate   : LocalDate.now().toString();
        String start = (startDate != null && !startDate.isEmpty()) ? startDate : LocalDate.now().minusDays(29).toString();
        return ApiResponse.ok(attendanceMapper.dailyTrend(start, end));
    }
}
