package com.vibeslop.backend.controller;

import com.vibeslop.backend.dto.InterviewQuestionRequest;
import com.vibeslop.backend.service.InterviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/generate")
    public String generateInterviewQuestions(@RequestBody InterviewQuestionRequest request) {
        return interviewService.generateQuestions(request.technologies());
    }
}