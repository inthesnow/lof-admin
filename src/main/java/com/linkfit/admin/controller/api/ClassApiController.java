package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.ClassAttendee;
import com.linkfit.admin.domain.ClassSession;
import com.linkfit.admin.domain.OnepointRequest;
import com.linkfit.admin.domain.TrainerSchedule;
import com.linkfit.admin.mapper.ClassMapper;
import com.linkfit.admin.mapper.OnepointRequestMapper;
import com.linkfit.admin.mapper.TrainerScheduleMapper;
import com.linkfit.admin.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/classes")
public class ClassApiController {

    private static final Logger log = LoggerFactory.getLogger(ClassApiController.class);

    private final ClassService classService;
    private final ClassMapper classMapper;
    private final TrainerScheduleMapper trainerScheduleMapper;
    private final OnepointRequestMapper onepointRequestMapper;

    public ClassApiController(ClassService classService, ClassMapper classMapper,
                              TrainerScheduleMapper trainerScheduleMapper,
                              OnepointRequestMapper onepointRequestMapper) {
        this.classService            = classService;
        this.classMapper             = classMapper;
        this.trainerScheduleMapper   = trainerScheduleMapper;
        this.onepointRequestMapper   = onepointRequestMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Class] GET /api/classes - type={}, date={}", type, date);
        return ApiResponse.ok(Map.of(
            "classes", classService.findAll(type, date, page, size),
            "total", classService.count(type, date)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassSession>> get(@PathVariable Long id) {
        log.info("[Class] GET /api/classes/{id} - id={}", id);
        return classService.findById(id)
            .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<ClassSession> create(@RequestBody ClassSession session) {
        log.info("[Class] POST /api/classes");
        return ApiResponse.ok(classService.save(session));
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassSession> update(@PathVariable Long id, @RequestBody ClassSession session) {
        log.info("[Class] PUT /api/classes/{id} - id={}", id);
        return ApiResponse.ok(classService.update(id, session));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        log.info("[Class] DELETE /api/classes/{id} - id={}", id);
        classService.cancel(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/attendees")
    public ApiResponse<List<ClassAttendee>> getAttendees(@PathVariable Long id) {
        log.info("[Class] GET /api/classes/{id}/attendees - id={}", id);
        return ApiResponse.ok(classMapper.findAttendees(id));
    }

    @PostMapping("/{id}/attendees")
    public ApiResponse<Void> enroll(@PathVariable Long id, @RequestBody Map<String, String> body) {
        log.info("[Class] POST /api/classes/{id}/attendees - id={}", id);
        classService.enroll(id, body.get("memberId"));
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/attendees/{memberId}")
    public ApiResponse<Void> cancelEnrollment(@PathVariable Long id, @PathVariable String memberId) {
        log.info("[Class] DELETE /api/classes/{id}/attendees/{memberId} - id={}, memberId={}", id, memberId);
        classService.cancelEnrollment(id, memberId);
        return ApiResponse.ok();
    }

    // ── 트레이너 일정 (trainer_schedules) ──────────────────────

    @GetMapping("/schedules")
    public ApiResponse<List<TrainerSchedule>> schedules(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate ref = LocalDate.now();
        int y = (year  != null) ? year  : ref.getYear();
        int m = (month != null) ? month : ref.getMonthValue();
        log.info("[Class] GET /api/classes/schedules - year={}, month={}", y, m);
        return ApiResponse.ok(trainerScheduleMapper.findByMonth(y, m));
    }

    @GetMapping("/schedules/date")
    public ApiResponse<List<TrainerSchedule>> schedulesByDate(@RequestParam String date) {
        log.info("[Class] GET /api/classes/schedules/date - date={}", date);
        return ApiResponse.ok(trainerScheduleMapper.findByDate(date));
    }

    // ── 원포인트 신청 (onepoint_requests) ─────────────────────

    @GetMapping("/onepoint/requests")
    public ApiResponse<Map<String, Object>> onepointRequests(
            @RequestParam(defaultValue = "")  String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("[Class] GET /api/classes/onepoint/requests - status={}", status);
        List<OnepointRequest> list = onepointRequestMapper.findAll(status, page * size, size);
        long total                 = onepointRequestMapper.count(status);
        return ApiResponse.ok(Map.of("requests", list, "total", total, "page", page));
    }

    @PatchMapping("/onepoint/requests/{id}/status")
    public ApiResponse<Void> updateOnepointStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("[Class] PATCH /api/classes/onepoint/requests/{}/status - status={}", id, body.get("status"));
        onepointRequestMapper.updateStatus(id, body.get("status"), body.get("note"));
        return ApiResponse.ok();
    }
}
