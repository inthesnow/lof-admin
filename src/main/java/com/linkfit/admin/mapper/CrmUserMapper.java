package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.CrmUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CrmUserMapper {

    Optional<CrmUser> findByBranchCodeAndUsername(
            @Param("branchCode") String branchCode,
            @Param("username")   String username);

    Optional<CrmUser> findById(@Param("id") String id);

    Optional<CrmUser> findByGymIdAndUsername(
            @Param("gymId")    Long gymId,
            @Param("username") String username);

    int insert(CrmUser user);
}
