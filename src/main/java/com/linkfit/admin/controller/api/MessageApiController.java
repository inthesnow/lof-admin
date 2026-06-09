package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Message;
import com.linkfit.admin.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    private static final Logger log = LoggerFactory.getLogger(MessageApiController.class);

    private final MessageService messageService;

    public MessageApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Message] GET /api/messages - page={}, size={}", page, size);
        return ApiResponse.ok(Map.of(
            "messages", messageService.findAll(page, size),
            "total", messageService.count()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> get(@PathVariable Long id) {
        log.info("[Message] GET /api/messages/{id} - id={}", id);
        return messageService.findById(id)
            .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Message> send(@RequestBody Message message) {
        log.info("[Message] POST /api/messages");
        return ApiResponse.ok(messageService.send(message));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        log.info("[Message] DELETE /api/messages/{id} - id={}", id);
        messageService.delete(id);
        return ApiResponse.ok();
    }
}
