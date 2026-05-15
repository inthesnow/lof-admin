package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Staff;
import com.linkfit.admin.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffApiController {

    private final StaffService staffService;

    public StaffApiController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Staff> staff = staffService.findAll(role, page, size);
        long total = staffService.count(role);
        return ApiResponse.ok(Map.of("staff", staff, "total", total));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Staff>> get(@PathVariable Long id) {
        return staffService.findById(id)
            .map(s -> ResponseEntity.ok(ApiResponse.ok(s)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Staff> create(@RequestBody Staff staff) {
        return ApiResponse.ok(staffService.save(staff));
    }

    @PutMapping("/{id}")
    public ApiResponse<Staff> update(@PathVariable Long id, @RequestBody Staff staff) {
        return ApiResponse.ok(staffService.update(id, staff));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        staffService.updateRole(id, body.get("role"));
        return ApiResponse.ok();
    }
}
