package com.travelmind.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.travelmind.dto.ChatRequest;
import com.travelmind.service.OrchestratorService;

/**
 * The protected endpoint: POST /chat -> validates input, then delegates to the
 * orchestrator. Requires a valid JWT (see AuthController to obtain one).
 */
@RestController
public class ChatController {

    private final OrchestratorService orchestratorService;

    public ChatController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest) {
        if (chatRequest.message() == null || chatRequest.message().isBlank()) {
            throw new IllegalArgumentException("message must not be empty");
        }
        if (chatRequest.message().length() > 2000) {
            throw new IllegalArgumentException("message is too long (max 2000 characters)");
        }
        String conversationId = (chatRequest.conversationId() == null || chatRequest.conversationId().isBlank())
                ? "default"
                : chatRequest.conversationId();
        return orchestratorService.chat(chatRequest.message(), conversationId);
    }
}
