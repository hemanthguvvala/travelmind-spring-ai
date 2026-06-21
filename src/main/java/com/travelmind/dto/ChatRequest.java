package com.travelmind.dto;

/**
 * Request body for POST /chat, e.g. {"message": "...", "conversationId": "abc"}.
 * conversationId is optional (defaults to "default").
 */
public record ChatRequest(String message, String conversationId) {
}
