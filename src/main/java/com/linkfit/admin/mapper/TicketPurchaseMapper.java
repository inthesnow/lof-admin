package com.linkfit.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TicketPurchaseMapper {
    List<Map<String, Object>> findAll(@Param("productId") String productId,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    long count(@Param("productId") String productId,
               @Param("startDate") String startDate,
               @Param("endDate") String endDate);

    List<Map<String, Object>> statsByType(@Param("months") int months);
}
