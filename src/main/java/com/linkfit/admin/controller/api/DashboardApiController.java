package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/members")
    public ApiResponse<Map<String, Object>> memberStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        return ApiResponse.ok(dashboardService.memberStats(date, period));
    }

    @GetMapping("/consults")
    public ApiResponse<Map<String, Object>> consultStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(dashboardService.consultStats(date, period));
    }

    @GetMapping("/classes")
    public ApiResponse<Map<String, Object>> classStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        return ApiResponse.ok(dashboardService.classStats(date, period));
    }

    @GetMapping("/revenue")
    public ApiResponse<Map<String, Object>> revenueStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(dashboardService.revenueStats(date, period));
    }

    @GetMapping("/revenue/{category}")
    public ApiResponse<Map<String, Object>> revenueDetail(
            @PathVariable String category,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(dashboardService.revenueDetail(category, date, period));
    }

    @GetMapping("/attendance")
    public ApiResponse<Map<String, Object>> attendanceStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        return ApiResponse.ok(dashboardService.attendanceStats(date, period, type));
    }
}
