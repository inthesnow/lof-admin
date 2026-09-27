package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.SettlementBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementBatchMapper {

    /** 이 지점(gymId)에 대한 정산 배치 목록 — 항상 gymId로 스코핑, 다른 지점 데이터는 절대 조회 안 됨. */
    List<SettlementBatch> findByGym(@Param("gymId") Long gymId);
}
