package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Member;
import com.linkfit.admin.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String tier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Member> members = memberService.findAll(keyword, status, page, size);
        long total = memberService.count(keyword, status);
        return ApiResponse.ok(Map.of("members", members, "total", total, "page", page, "size", size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> get(@PathVariable String id) {
        return memberService.findById(id)
            .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Member> create(@RequestBody Member member) {
        return ApiResponse.ok(memberService.save(member));
    }

    @PutMapping("/{id}")
    public ApiResponse<Member> update(@PathVariable String id, @RequestBody Member member) {
        return ApiResponse.ok(memberService.update(id, member));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        memberService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        memberService.updateStatus(id, body.get("status"));
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/tier")
    public ApiResponse<Void> updateTier(@PathVariable String id, @RequestBody Map<String, String> body) {
        memberService.updateTier(id, body.get("tier"));
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/member-type")
    public ApiResponse<Void> updateMemberType(@PathVariable String id, @RequestBody Map<String, String> body) {
        memberService.updateMemberType(id, body.get("memberType"));
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/freeze")
    public ApiResponse<Void> freeze(@PathVariable String id, @RequestBody Map<String, String> body) {
        memberService.freeze(id, body.get("startDate"), body.get("endDate"));
        return ApiResponse.ok();
    }
}
