package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.GymBanner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GymBannerMapper {
    List<GymBanner> findAll();
    void insert(GymBanner banner);
    void delete(@Param("id") Long id);
    void updateSortOrder(@Param("id") Long id, @Param("sortOrder") int sortOrder);
    void toggleActive(@Param("id") Long id, @Param("isActive") boolean isActive);
}
