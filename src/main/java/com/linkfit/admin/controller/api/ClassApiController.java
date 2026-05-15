package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.ClassSession;
import com.linkfit.admin.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class ClassApiController {

    private final ClassService classService;

    public ClassApiController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(Map.of(
            "classes", classService.findAll(type, date, page, size),
            "total", classService.count(type, date)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassSession>> get(@PathVariable Long id) {
        return classService.findById(id)
            .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<ClassSession> create(@RequestBody ClassSession session) {
        return ApiResponse.ok(classService.save(session));
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassSession> update(@PathVariable Long id, @RequestBody ClassSession session) {
        return ApiResponse.ok(classService.update(id, session));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        classService.cancel(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/attendees")
    public ApiResponse<Void> enroll(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        classService.enroll(id, body.get("memberId"));
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/attendees/{memberId}")
    public ApiResponse<Void> cancelEnrollment(@PathVariable Long id, @PathVariable Long memberId) {
        classService.cancelEnrollment(id, memberId);
        return ApiResponse.ok();
    }
}
