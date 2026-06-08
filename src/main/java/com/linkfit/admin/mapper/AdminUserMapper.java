package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdminUserMapper {
    Optional<AdminUser> findByBranchCodeAndUsername(
            @Param("branchCode") String branchCode,
            @Param("username")   String username);
}
