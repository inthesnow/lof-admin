package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.ReRegistration;
import com.linkfit.admin.security.CrmUserDetails;
import com.linkfit.admin.service.ReRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reregistration")
public class ReRegistrationApiController {

    private final ReRegistrationService service;

    public ReRegistrationApiController(ReRegistrationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String reason,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CrmUserDetails principal) {
        Long gymId = principal.getGymId();
        List<ReRegistration> list = service.findAll(gymId, status, reason, page, size);
        long total = service.count(gymId, status, reason);
        return ApiResponse.ok(Map.of("items", list, "total", total, "page", page, "size", size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReRegistration>> get(@PathVariable String id) {
        return service.findById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        service.updateStatus(id, body.get("status"));
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/memo")
    public ApiResponse<Void> updateMemo(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        service.updateMemo(id, body.get("memo"));
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/assign")
    public ApiResponse<Void> assign(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        service.assign(id, body.get("assignedTo"));
        return ApiResponse.ok();
    }

    @PostMapping("/auto-classify")
    public ApiResponse<Map<String, Object>> autoClassify(
            @AuthenticationPrincipal CrmUserDetails principal) {
        int created = service.autoClassify(principal.getGymId());
        return ApiResponse.ok(Map.of("created", created));
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Integer>> summary(
            @AuthenticationPrincipal CrmUserDetails principal) {
        return ApiResponse.ok(service.statusSummary(principal.getGymId()));
    }
}
