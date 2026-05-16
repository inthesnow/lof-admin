package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SaleMapper {
    Map<String, Object> revenueStats(@Param("date") String date, @Param("period") String period);
    List<Map<String, Object>> revenueDetail(@Param("category") String category,
                                            @Param("date") String date,
                                            @Param("period") String period);
    void insert(Sale sale);
}
