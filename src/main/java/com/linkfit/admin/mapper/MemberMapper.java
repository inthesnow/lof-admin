package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    List<Member> findAll(@Param("keyword") String keyword, @Param("status") String status,
                         @Param("offset") int offset, @Param("size") int size);
    long count(@Param("keyword") String keyword, @Param("status") String status);
    Optional<Member> findById(@Param("id") Long id);
    void insert(Member member);
    void update(Member member);
    void delete(@Param("id") Long id);
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
