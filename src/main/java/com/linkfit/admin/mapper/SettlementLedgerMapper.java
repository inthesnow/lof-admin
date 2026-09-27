package com.linkfit.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SettlementLedgerMapper {

    /**
     * 카운터(현장 결제) 매출 1건을 정산 원장에 기록 — 헬스장이 이미 현금/카드로 직접 수금한
     * 거래라 정산 배치 계산 대상에서는 제외되지만(recognized_at을 즉시 채워 PENDING_USAGE로
     * 남지 않게 함으로써 명확히 구분), 채널별 매출 통계/보고를 위해 로그만 남긴다.
     * gym_id는 로그인한 관리자의 소속 지점을 그대로 쓰므로(카운터 결제는 그 지점 직원이
     * 직접 입력) 다른 채널과 달리 별도 조회가 필요 없다.
     */
    void insertCounterSale(@Param("gymId") Long gymId,
                            @Param("memberId") String memberId,
                            @Param("productId") String productId,
                            @Param("productType") String productType,
                            @Param("amount") int amount,
                            @Param("externalTransactionId") String externalTransactionId);
}
