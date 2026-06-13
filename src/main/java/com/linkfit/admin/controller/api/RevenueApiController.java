package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.mapper.SaleMapper;
import com.linkfit.admin.mapper.TicketPurchaseMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/revenue")
public class RevenueApiController {

    private static final Logger log = LoggerFactory.getLogger(RevenueApiController.class);

    private final SaleMapper saleMapper;
    private final TicketPurchaseMapper ticketPurchaseMapper;

    public RevenueApiController(SaleMapper saleMapper, TicketPurchaseMapper ticketPurchaseMapper) {
        this.saleMapper            = saleMapper;
        this.ticketPurchaseMapper  = ticketPurchaseMapper;
    }

    // ── 기존: 카테고리별 요약 ──────────────────────────────────────

    @GetMapping
    public ApiResponse<Map<String, Object>> stats(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "monthly") String period) {
        log.info("[Revenue] GET /api/revenue - date={}, period={}", date, period);
        Map<String, Object> result = saleMapper.revenueStats(date, period);
        return ApiResponse.ok(result != null ? result
                : Map.of("membership", 0, "groupClass", 0, "pt", 0, "locker", 0, "items", 0, "total", 0));
    }

    @GetMapping("/{category}")
    public ApiResponse<Map<String, Object>> detail(
            @PathVariable String category,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "monthly") String period) {
        log.info("[Revenue] GET /api/revenue/{category} - category={}, date={}, period={}", category, date, period);
        List<Map<String, Object>> items = saleMapper.revenueDetail(category, date, period);
        long total = items.stream()
                .mapToLong(r -> r.get("amount") instanceof Number n ? n.longValue() : 0)
                .sum();
        return ApiResponse.ok(Map.of("category", category, "items", items, "total", total));
    }

    // ── 월별 매출 추이 ────────────────────────────────────────────

    @GetMapping("/trend")
    public ApiResponse<List<Map<String, Object>>> trend(
            @RequestParam(defaultValue = "6") int months) {
        log.info("[Revenue] GET /api/revenue/trend - months={}", months);
        return ApiResponse.ok(saleMapper.monthlyTrend(months));
    }

    // ── 결제 내역 페이지네이션 ────────────────────────────────────

    @GetMapping("/sales")
    public ApiResponse<Map<String, Object>> sales(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String startDate,
            @RequestParam(defaultValue = "") String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Revenue] GET /api/revenue/sales - type={}, start={}, end={}", type, startDate, endDate);
        List<Map<String, Object>> list = saleMapper.findPaged(type, startDate, endDate, page * size, size);
        long total = saleMapper.countPaged(type, startDate, endDate);
        return ApiResponse.ok(Map.of("sales", list, "total", total, "page", page));
    }

    // ── 환불 처리 ─────────────────────────────────────────────────

    @DeleteMapping("/sales/{id}")
    public ApiResponse<Void> deleteSale(@PathVariable Long id) {
        log.info("[Revenue] DELETE /api/revenue/sales/{id} - id={}", id);
        saleMapper.deleteById(id);
        return ApiResponse.ok();
    }

    // ── CSV 내보내기 ──────────────────────────────────────────────

    @GetMapping("/sales/export")
    public void exportCsv(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String startDate,
            @RequestParam(defaultValue = "") String endDate,
            HttpServletResponse response) throws IOException {
        log.info("[Revenue] GET /api/revenue/sales/export - type={}, start={}, end={}", type, startDate, endDate);
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"revenue_" + LocalDate.now() + ".csv\"");

        List<Map<String, Object>> data = saleMapper.findForExport(type, startDate, endDate);

        // BOM for Excel UTF-8
        response.getOutputStream().write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
        PrintWriter pw = new PrintWriter(response.getWriter());
        pw.println("번호,날짜,회원명,상품명,유형,금액,결제수단,메모");
        int i = 1;
        for (Map<String, Object> r : data) {
            pw.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    i++,
                    r.getOrDefault("saleDate", ""),
                    esc(r.get("memberName")),
                    esc(r.get("productName")),
                    esc(r.get("productType")),
                    r.getOrDefault("amount", 0),
                    esc(r.get("paymentMethod")),
                    esc(r.get("memo")));
        }
        pw.flush();
    }

    // ── 구독(Tier) 분포 ──────────────────────────────────────────

    @GetMapping("/subscriptions/stats")
    public ApiResponse<List<Map<String, Object>>> subscriptionStats() {
        log.info("[Revenue] GET /api/revenue/subscriptions/stats");
        return ApiResponse.ok(saleMapper.tierDistribution());
    }

    // ── 티켓 판매 내역 ────────────────────────────────────────────

    @GetMapping("/tickets")
    public ApiResponse<Map<String, Object>> tickets(
            @RequestParam(defaultValue = "") String productId,
            @RequestParam(defaultValue = "") String startDate,
            @RequestParam(defaultValue = "") String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Revenue] GET /api/revenue/tickets - productId={}", productId);
        List<Map<String, Object>> list = ticketPurchaseMapper.findAll(productId, startDate, endDate, page * size, size);
        long total = ticketPurchaseMapper.count(productId, startDate, endDate);
        return ApiResponse.ok(Map.of("tickets", list, "total", total, "page", page));
    }

    @GetMapping("/tickets/stats")
    public ApiResponse<List<Map<String, Object>>> ticketStats(
            @RequestParam(defaultValue = "6") int months) {
        log.info("[Revenue] GET /api/revenue/tickets/stats - months={}", months);
        return ApiResponse.ok(ticketPurchaseMapper.statsByType(months));
    }

    // ── helper ──────────────────────────────────────────────────

    private String esc(Object v) {
        if (v == null) return "";
        String s = v.toString().replace("\"", "\"\"");
        return s.contains(",") || s.contains("\n") || s.contains("\"") ? "\"" + s + "\"" : s;
    }
}
