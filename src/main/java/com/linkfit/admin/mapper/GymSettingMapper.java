package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.GymSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GymSettingMapper {
    GymSetting find();
    void upsert(GymSetting setting);
    void updateOpenStatus(@Param("isOpen") boolean isOpen);
}
