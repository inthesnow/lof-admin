package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.MemberTicket;
import com.linkfit.admin.domain.PtMember;
import com.linkfit.admin.mapper.MemberMapper;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/pt")
public class PtApiController {

    private static final Logger log = LoggerFactory.getLogger(PtApiController.class);

    private final MemberMapper memberMapper;

    public PtApiController(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @GetMapping("/members")
    public ApiResponse<Map<String, Object>> listPtMembers(
            @RequestParam(defaultValue = "false") boolean lowStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Pt] GET /api/pt/members - lowStock={}, page={}", lowStock, page);
        int offset = page * size;
        List<PtMember> items = memberMapper.findPtMembers(lowStock, offset, size);
        long total = memberMapper.countPtMembers(lowStock);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("items", items);
        data.put("total", total);
        return ApiResponse.ok(data);
    }

    @GetMapping("/members/{memberId}/tickets")
    public ApiResponse<List<MemberTicket>> getMemberTickets(@PathVariable String memberId) {
        log.info("[Pt] GET /api/pt/members/{memberId}/tickets - memberId={}", memberId);
        return ApiResponse.ok(memberMapper.findTickets(memberId));
    }

    @PutMapping("/members/{memberId}/tickets")
    public ApiResponse<Void> adjustTicket(@PathVariable String memberId,
                                           @RequestBody Map<String, Object> body) {
        log.info("[Pt] PUT /api/pt/members/{memberId}/tickets - memberId={}", memberId);
        String ticketType = (String) body.get("ticketType");
        int amount = ((Number) body.get("amount")).intValue();
        String actionType = (String) body.getOrDefault("actionType", "adjust");
        String description = (String) body.getOrDefault("description", "");

        memberMapper.upsertTicket(memberId, ticketType, amount);
        memberMapper.insertTicketLog(memberId, ticketType, actionType, description);
        return ApiResponse.ok();
    }
}
