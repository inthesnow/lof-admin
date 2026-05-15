package com.linkfit.admin.controller.api;

import com.linkfit.admin.common.ApiResponse;
import com.linkfit.admin.domain.Message;
import com.linkfit.admin.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    private final MessageService messageService;

    public MessageApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(Map.of(
            "messages", messageService.findAll(page, size),
            "total", messageService.count()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> get(@PathVariable Long id) {
        return messageService.findById(id)
            .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<Message> send(@RequestBody Message message) {
        return ApiResponse.ok(messageService.send(message));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ApiResponse.ok();
    }
}
