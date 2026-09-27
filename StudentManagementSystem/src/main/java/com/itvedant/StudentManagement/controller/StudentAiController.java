package com.itvedant.StudentManagement.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itvedant.StudentManagement.dto.AIRequestDTO;
import com.itvedant.StudentManagement.services.AIService;

import reactor.core.publisher.Flux;

@Controller
public class StudentAiController {

    private final AIService aiService;

    public StudentAiController(AIService aiService) {
        this.aiService = aiService;
    }

    // =========================================================
    // PAGE
    // =========================================================

    @GetMapping("/student/ai-assistant")
    public String aiAssistant() {
        return "student/ai-assistant";
    }

    // =========================================================
    // STREAMING CHAT ENDPOINT — same as admin
    // =========================================================

    @PostMapping(
        value = "/student/ai-assistant/chat",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @ResponseBody
    public Flux<String> chat(@RequestBody AIRequestDTO request) {

        return aiService.askAI(request.getMessage());
    }
}