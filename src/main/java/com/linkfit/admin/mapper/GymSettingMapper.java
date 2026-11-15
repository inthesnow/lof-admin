package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.GymSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GymSettingMapper {
    GymSetting find(@Param("gymId") Long gymId);
    void upsert(@Param("setting") GymSetting setting, @Param("gymId") Long gymId);
    void updateOpenStatus(@Param("isOpen") boolean isOpen, @Param("gymId") Long gymId);

    /**
     * 설정 화면의 "헬스장 이름"을 gym.name에도 반영한다 (2026-11-15 crm-8).
     *
     * 그동안 관리자 입력은 gym_setting.gym_name에만 저장됐고, 본사 포탈과
     * 회원에게 가는 쪽지 발신자명은 gym.name을 읽어 둘이 갈라져 있었다.
     * 팀 결정에 따라 관리자가 입력한 이름을 정본으로 삼는다.
     */
    void syncGymName(@Param("name") String name, @Param("gymId") Long gymId);
}
