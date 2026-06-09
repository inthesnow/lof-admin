package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Consult;
import com.linkfit.admin.service.ConsultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/consults")
public class ConsultApiController {

    private static final Logger log = LoggerFactory.getLogger(ConsultApiController.class);

    private final ConsultService consultService;

    public ConsultApiController(ConsultService consultService) {
        this.consultService = consultService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Consult] GET /api/consults - type={}, page={}", type, page);
        return ApiResponse.ok(Map.of(
            "consults", consultService.findAll(type, page, size),
            "total", consultService.count(type)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Consult>> get(@PathVariable Long id) {
        log.info("[Consult] GET /api/consults/{id} - id={}", id);
        return consultService.findById(id)
            .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Consult> createNew(@RequestBody Consult consult) {
        log.info("[Consult] POST /api/consults");
        return ApiResponse.ok(consultService.saveNew(consult));
    }

    @PostMapping("/existing")
    public ApiResponse<Consult> createExisting(@RequestBody Consult consult) {
        log.info("[Consult] POST /api/consults/existing");
        return ApiResponse.ok(consultService.saveExisting(consult));
    }

    @PutMapping("/{id}")
    public ApiResponse<Consult> update(@PathVariable Long id, @RequestBody Consult consult) {
        log.info("[Consult] PUT /api/consults/{id} - id={}", id);
        return ApiResponse.ok(consultService.update(id, consult));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("[Consult] DELETE /api/consults/{id} - id={}", id);
        consultService.delete(id);
        return ApiResponse.ok();
    }
}
