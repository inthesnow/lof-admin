package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.GymSetting;
import com.linkfit.admin.mapper.GymSettingMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingApiController {

    private final GymSettingMapper gymSettingMapper;

    public SettingApiController(GymSettingMapper gymSettingMapper) {
        this.gymSettingMapper = gymSettingMapper;
    }

    @GetMapping("/gym")
    public ApiResponse<GymSetting> get() {
        GymSetting setting = gymSettingMapper.find();
        if (setting == null) setting = new GymSetting();
        return ApiResponse.ok(setting);
    }

    @PutMapping("/gym")
    public ApiResponse<Void> save(@RequestBody GymSetting setting) {
        gymSettingMapper.upsert(setting);
        return ApiResponse.ok();
    }

    @PatchMapping("/gym/open")
    public ApiResponse<Void> toggleOpen(@RequestBody Map<String, Boolean> body) {
        boolean open = Boolean.TRUE.equals(body.get("isOpen"));
        gymSettingMapper.updateOpenStatus(open);
        return ApiResponse.ok();
    }
}
