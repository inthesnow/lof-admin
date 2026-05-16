package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.mapper.SaleMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
public class RevenueApiController {

    private final SaleMapper saleMapper;

    public RevenueApiController(SaleMapper saleMapper) {
        this.saleMapper = saleMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> stats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "monthly") String period) {
        Map<String, Object> result = saleMapper.revenueStats(date, period);
        return ApiResponse.ok(result != null ? result
                : Map.of("membership", 0, "groupClass", 0, "pt", 0, "locker", 0, "items", 0, "total", 0));
    }

    @GetMapping("/{category}")
    public ApiResponse<Map<String, Object>> detail(
            @PathVariable String category,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "monthly") String period) {
        List<Map<String, Object>> items = saleMapper.revenueDetail(category, date, period);
        long total = items.stream()
                .mapToLong(r -> r.get("amount") instanceof Number n ? n.longValue() : 0)
                .sum();
        return ApiResponse.ok(Map.of("category", category, "items", items, "total", total));
    }
}
