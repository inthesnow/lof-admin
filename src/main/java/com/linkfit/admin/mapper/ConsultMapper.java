package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Consult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface ConsultMapper {
    List<Consult> findAll(@Param("type") String type, @Param("offset") int offset, @Param("size") int size);
    long count(@Param("type") String type);
    Optional<Consult> findById(@Param("id") Long id);
    void insert(Consult consult);
    void update(Consult consult);
    void delete(@Param("id") Long id);
    Map<String, Object> countStats(@Param("date") String date, @Param("period") String period);
}
