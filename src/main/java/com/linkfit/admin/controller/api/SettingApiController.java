package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.GymSetting;
import com.linkfit.admin.mapper.GymSettingMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/settings")
public class SettingApiController {

    private static final Logger log = LoggerFactory.getLogger(SettingApiController.class);

    private final GymSettingMapper gymSettingMapper;

    public SettingApiController(GymSettingMapper gymSettingMapper) {
        this.gymSettingMapper = gymSettingMapper;
    }

    @GetMapping("/gym")
    public ApiResponse<GymSetting> get() {
        log.info("[Setting] GET /api/settings/gym");
        GymSetting setting = gymSettingMapper.find();
        if (setting == null) setting = new GymSetting();
        return ApiResponse.ok(setting);
    }

    @PutMapping("/gym")
    public ApiResponse<Void> save(@RequestBody GymSetting setting) {
        log.info("[Setting] PUT /api/settings/gym");
        gymSettingMapper.upsert(setting);
        return ApiResponse.ok();
    }

    @PatchMapping("/gym/open")
    public ApiResponse<Void> toggleOpen(@RequestBody Map<String, Boolean> body) {
        log.info("[Setting] PATCH /api/settings/gym/open");
        boolean open = Boolean.TRUE.equals(body.get("isOpen"));
        gymSettingMapper.updateOpenStatus(open);
        return ApiResponse.ok();
    }
}
