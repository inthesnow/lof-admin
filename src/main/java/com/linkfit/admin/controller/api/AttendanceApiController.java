package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Attendance;
import com.linkfit.admin.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ApiResponse<List<Attendance>> list(
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "daily") String period) {
        return ApiResponse.ok(attendanceService.findAll(date, period));
    }

    @PostMapping
    public ApiResponse<Attendance> checkIn(@RequestBody Attendance attendance) {
        return ApiResponse.ok(attendanceService.checkIn(attendance));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        attendanceService.cancel(id);
        return ApiResponse.ok();
    }

    @GetMapping("/freeze")
    public ApiResponse<List<Attendance>> frozen(@RequestParam(defaultValue = "") String date) {
        return ApiResponse.ok(attendanceService.findFrozen(date));
    }
}
