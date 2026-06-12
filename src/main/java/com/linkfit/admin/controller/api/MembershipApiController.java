package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.CrmMembershipHistory;
import com.linkfit.admin.domain.Membership;
import com.linkfit.admin.mapper.MemberMapper;

import com.linkfit.admin.security.CrmUserDetails;
import com.linkfit.admin.service.CrmMemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/memberships")
public class MembershipApiController {

    private static final Logger log = LoggerFactory.getLogger(MembershipApiController.class);

    private final MemberMapper memberMapper;
    private final CrmMemberService crmMemberService;

    public MembershipApiController(MemberMapper memberMapper, CrmMemberService crmMemberService) {
        this.memberMapper     = memberMapper;
        this.crmMemberService = crmMemberService;
    }

    @GetMapping("/expiring")
    public ApiResponse<Map<String, Object>> expiring(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Membership] GET /api/memberships/expiring - days={}, page={}", days, page);
        List<Membership> list  = memberMapper.findExpiringMemberships(days, page * size, size);
        long total             = memberMapper.countExpiringMemberships(days);
        return ApiResponse.ok(Map.of("memberships", list, "total", total, "page", page, "size", size, "days", days));
    }

    @GetMapping("/member/{memberId}/history")
    public ApiResponse<List<CrmMembershipHistory>> getHistory(
            @PathVariable String memberId,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Membership] GET /api/memberships/member/{memberId}/history - memberId={}", memberId);
        return ApiResponse.ok(crmMemberService.findMembershipHistory(memberId, principal.getGymId()));
    }

    @PatchMapping("/{id}/end-date")
    public ApiResponse<Void> updateEndDate(@PathVariable Long id, @RequestBody Map<String, String> body) {
        log.info("[Membership] PATCH /api/memberships/{}/end-date", id);
        memberMapper.updateMembershipEndDate(id, body.get("endDate"));
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("[Membership] DELETE /api/memberships/{}", id);
        memberMapper.deleteMembership(id);
        return ApiResponse.ok();
    }

    @PostMapping("/member/{memberId}/actions")
    public ApiResponse<Void> recordAction(
            @PathVariable String memberId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CrmUserDetails principal) {
        log.info("[Membership] POST /api/memberships/member/{memberId}/actions - memberId={}", memberId);
        crmMemberService.recordMembershipAction(
                memberId, principal.getGymId(),
                body.get("action"), body.get("reason"),
                principal.getId());
        return ApiResponse.ok();
    }
}
