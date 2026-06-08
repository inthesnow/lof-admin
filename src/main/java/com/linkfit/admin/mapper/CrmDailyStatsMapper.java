package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.CrmDailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CrmDailyStatsMapper {

    void upsert(CrmDailyStats stats);

    void aggregate(@Param("gymId") Long gymId, @Param("statDate") String statDate);

    List<Long> findAllGymIds();

    List<CrmDailyStats> findRecent(@Param("gymId") Long gymId,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate);

    CrmDailyStats findByDate(@Param("gymId") Long gymId, @Param("statDate") String statDate);
}
