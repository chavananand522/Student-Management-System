package com.itvedant.StudentManagement.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.itvedant.StudentManagement.dto.AIRequestDTO;
import com.itvedant.StudentManagement.services.AIService;

import reactor.core.publisher.Flux;

@Controller
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ai-assistant")
    public String aiAssistant() {
        return "ai-assistant";
    }

    @PostMapping(value = "/ai-assistant/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> chat(@RequestBody AIRequestDTO request) {

        return aiService.askAI(request.getMessage());

    }

}