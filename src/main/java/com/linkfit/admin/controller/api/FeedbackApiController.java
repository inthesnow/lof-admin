package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.FeedbackRequest;
import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.domain.TicketSettings;
import com.linkfit.admin.security.CrmUserDetails;
import com.linkfit.admin.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackApiController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackApiController.class);

    private final FeedbackService feedbackService;

    public FeedbackApiController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ── Sector 7: 티켓 관리 ─────────────────────────────────

    @GetMapping("/tickets")
    public ApiResponse<Map<String, Object>> listTickets(
            @RequestParam(defaultValue = "") String monthYear,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] GET /api/feedback/tickets - monthYear={}, status={}", monthYear, status);
        Long gymId = principal.getGymId();
        List<FeedbackTicket> list = feedbackService.findTickets(gymId, monthYear, status, page, size);
        long total = feedbackService.countTickets(gymId, monthYear, status);
        return ApiResponse.ok(Map.of("tickets", list, "total", total, "page", page, "size", size));
    }

    @PostMapping("/tickets/issue")
    public ApiResponse<FeedbackTicket> issueTicket(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] POST /api/feedback/tickets/issue");
        return ApiResponse.ok(feedbackService.issueTicket(
                body.get("memberId"), principal.getGymId(),
                body.getOrDefault("ticketType", "free"),
                body.get("monthYear")));
    }

    @PatchMapping("/tickets/{id}/status")
    public ApiResponse<Void> updateTicketStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        log.info("[Feedback] PATCH /api/feedback/tickets/{id}/status - id={}", id);
        feedbackService.updateTicketStatus(id, body.get("status"));
        return ApiResponse.ok();
    }

    @PatchMapping("/tickets/{id}/trainer")
    public ApiResponse<Void> assignTicketTrainer(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        log.info("[Feedback] PATCH /api/feedback/tickets/{id}/trainer - id={}", id);
        feedbackService.assignTicketTrainer(id, body.get("trainerId"));
        return ApiResponse.ok();
    }

    @GetMapping("/tickets/summary")
    public ApiResponse<Map<String, Integer>> usageSummary(
            @RequestParam(defaultValue = "") String monthYear,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] GET /api/feedback/tickets/summary - monthYear={}", monthYear);
        return ApiResponse.ok(feedbackService.usageSummary(principal.getGymId(), monthYear));
    }

    // ── Sector 8: 티켓 설정 ─────────────────────────────────

    @GetMapping("/settings")
    public ApiResponse<TicketSettings> getSettings(
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] GET /api/feedback/settings");
        return ApiResponse.ok(feedbackService.getSettings(principal.getGymId()).orElse(null));
    }

    @PutMapping("/settings")
    public ApiResponse<TicketSettings> updateSettings(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] PUT /api/feedback/settings");
        int freePerMember   = body.containsKey("freeTicketsPerMember") ? (int) body.get("freeTicketsPerMember") : 2;
        Integer maxPerMonth = body.containsKey("maxTicketsPerMonth") ? (Integer) body.get("maxTicketsPerMonth") : null;
        boolean isBeta      = !Boolean.FALSE.equals(body.get("isBeta"));
        return ApiResponse.ok(feedbackService.updateSettings(
                principal.getGymId(), freePerMember, maxPerMonth, isBeta, principal.getId()));
    }

    // ── Sector 10: 피드백 요청 관리 ─────────────────────────

    @GetMapping("/requests")
    public ApiResponse<Map<String, Object>> listRequests(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String trainerId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Feedback] GET /api/feedback/requests - status={}, trainerId={}", status, trainerId);
        Long gymId = principal.getGymId();
        List<FeedbackRequest> list = feedbackService.findRequests(gymId, status, trainerId, page, size);
        long total = feedbackService.countRequests(gymId, status, trainerId);
        return ApiResponse.ok(Map.of("requests", list, "total", total, "page", page, "size", size));
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<ApiResponse<FeedbackRequest>> getRequest(@PathVariable String id) {
        log.info("[Feedback] GET /api/feedback/requests/{id} - id={}", id);
        return feedbackService.findRequestById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/requests/{id}/assign")
    public ApiResponse<Void> assignRequestTrainer(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        log.info("[Feedback] PATCH /api/feedback/requests/{id}/assign - id={}", id);
        feedbackService.assignRequestTrainer(id, body.get("trainerId"));
        return ApiResponse.ok();
    }

    @PostMapping("/requests/{id}/respond")
    public ApiResponse<Void> respond(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        log.info("[Feedback] POST /api/feedback/requests/{id}/respond - id={}", id);
        feedbackService.respondToRequest(id, body.get("response"));
        return ApiResponse.ok();
    }

    @PatchMapping("/requests/{id}/status")
    public ApiResponse<Void> updateRequestStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        log.info("[Feedback] PATCH /api/feedback/requests/{id}/status - id={}", id);
        feedbackService.updateRequestStatus(id, body.get("status"));
        return ApiResponse.ok();
    }
}
