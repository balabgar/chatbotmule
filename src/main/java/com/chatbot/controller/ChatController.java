package com.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chatbot.service.ChatService;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // POST request to receive the user's message and return the response
    @PostMapping
    public String chat(@RequestBody Map<String, String> body) {
        // Extract message from the request body
        String userMessage = body.get("message").toLowerCase();

        // Fetch response from the ChatService (which will interact with DynamoDB)
        return chatService.getResponse(userMessage);
    }
}
