package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Member;
import com.linkfit.admin.domain.Staff;
import com.linkfit.admin.mapper.StaffMapper;
import com.linkfit.admin.security.CrmUserDetails;
import com.linkfit.admin.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/staff")
public class StaffApiController {

    private static final Logger log = LoggerFactory.getLogger(StaffApiController.class);

    private final StaffService staffService;
    private final StaffMapper staffMapper;

    public StaffApiController(StaffService staffService, StaffMapper staffMapper) {
        this.staffService = staffService;
        this.staffMapper  = staffMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Staff] GET /api/staff - role={}, page={}", role, page);
        List<Staff> staff = staffService.findAll(role, page, size);
        long total = staffService.count(role);
        return ApiResponse.ok(Map.of("staff", staff, "total", total));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Staff>> get(@PathVariable String id) {
        log.info("[Staff] GET /api/staff/{id} - id={}", id);
        return staffService.findById(id)
            .map(s -> ResponseEntity.ok(ApiResponse.ok(s)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Staff> create(@RequestBody Staff staff) {
        log.info("[Staff] POST /api/staff");
        return ApiResponse.ok(staffService.save(staff));
    }

    @PutMapping("/{id}")
    public ApiResponse<Staff> update(@PathVariable String id, @RequestBody Staff staff) {
        log.info("[Staff] PUT /api/staff/{id} - id={}", id);
        return ApiResponse.ok(staffService.update(id, staff));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        log.info("[Staff] DELETE /api/staff/{id} - id={}", id);
        staffService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<Void> updateRole(@PathVariable String id, @RequestBody Map<String, String> body) {
        log.info("[Staff] PATCH /api/staff/{id}/role - id={}", id);
        staffService.updateRole(id, body.get("role"));
        return ApiResponse.ok();
    }

    // ── Sector 13: 트레이너 CRM 대시보드 ──────────────────────

    @GetMapping("/{id}/dashboard")
    public ApiResponse<Map<String, Object>> dashboard(
            @PathVariable String id,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Staff] GET /api/staff/{id}/dashboard - id={}", id);
        Map<String, Object> stats = staffMapper.findDashboard(id, principal.getGymId());
        return ApiResponse.ok(stats != null ? stats : Map.of(
                "assignedMembers", 0, "pendingFeedback", 0, "pendingReregistration", 0));
    }

    @GetMapping("/{id}/members")
    public ApiResponse<List<Member>> assignedMembers(
            @PathVariable String id,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Staff] GET /api/staff/{id}/members - id={}", id);
        return ApiResponse.ok(staffMapper.findAssignedMembers(id, principal.getGymId()));
    }
}
