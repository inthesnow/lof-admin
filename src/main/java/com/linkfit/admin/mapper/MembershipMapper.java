package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Membership;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MembershipMapper {
    List<Membership> findByMemberId(@Param("memberId") Long memberId);
    void insert(Membership membership);
}
