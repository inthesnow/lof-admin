package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @GetMapping("/members")
    public ApiResponse<Map<String, Object>> memberStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        var data = new java.util.LinkedHashMap<String, Object>();
        data.put("active", 97); data.put("expired", 1); data.put("newJoin", 1); data.put("reJoin", 0);
        data.put("maleActive", 43); data.put("femaleActive", 54);
        data.put("maleExpired", 1); data.put("femaleExpired", 0);
        data.put("maleNew", 0); data.put("femaleNew", 1);
        data.put("maleReJoin", 0); data.put("femaleReJoin", 0);
        return ApiResponse.ok(data);
    }

    @GetMapping("/consults")
    public ApiResponse<Map<String, Object>> consultStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(Map.of("newConsult", 0, "existingConsult", 0));
    }

    @GetMapping("/classes")
    public ApiResponse<Map<String, Object>> classStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        return ApiResponse.ok(Map.of(
            "total", Map.of("count", 5, "enrolled", 32),
            "헬스", Map.of("count", 2, "enrolled", 22),
            "필라테스", Map.of("count", 1, "enrolled", 8),
            "골프", Map.of("count", 1, "enrolled", 1),
            "기타", Map.of("count", 1, "enrolled", 1)
        ));
    }

    @GetMapping("/revenue")
    public ApiResponse<Map<String, Object>> revenueStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(Map.of(
            "membership", 0, "groupClass", 0, "pt", 0, "locker", 0, "items", 0, "total", 0
        ));
    }

    @GetMapping("/revenue/{category}")
    public ApiResponse<Map<String, Object>> revenueDetail(
            @PathVariable String category,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(Map.of("category", category, "items", java.util.List.of(), "total", 0));
    }

    @GetMapping("/attendance")
    public ApiResponse<Map<String, Object>> attendanceStats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "") String type) {
        return ApiResponse.ok(Map.of(
            "total", 23, "male", 11, "female", 12,
            "frozen", 1, "frozenMale", 1, "frozenFemale", 0
        ));
    }
}
