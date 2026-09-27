package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.SettlementBatch;
import com.linkfit.admin.mapper.SettlementBatchMapper;
import com.linkfit.admin.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 이 지점이 최고관리자로부터 받은 정산 내역 조회 전용(읽기 전용) — 실제 계산/지급 처리는
 * lof-potal이 담당한다. gymId는 항상 로그인한 관리자의 소속 지점(principal.getGymId())만
 * 쓴다 — 클라이언트가 다른 지점 id를 넘겨도 절대 조회되지 않는다.
 */
@RestController
@RequestMapping("/api/settlement")
public class SettlementApiController {

    private final SettlementBatchMapper settlementBatchMapper;

    public SettlementApiController(SettlementBatchMapper settlementBatchMapper) {
        this.settlementBatchMapper = settlementBatchMapper;
    }

    @GetMapping("/batches")
    public ApiResponse<List<SettlementBatch>> list(@AuthenticationPrincipal CrmUserDetails principal) {
        return ApiResponse.ok(settlementBatchMapper.findByGym(principal.getGymId()));
    }
}
