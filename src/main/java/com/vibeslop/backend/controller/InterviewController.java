package com.vibeslop.backend.controller;

import com.vibeslop.backend.dto.InterviewQuestionRequest;
import com.vibeslop.backend.dto.InterviewRequestDto;
import com.vibeslop.backend.dto.InterviewResponseDto;
import com.vibeslop.backend.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/generate")
    public String generateInterviewQuestions(@RequestBody InterviewQuestionRequest request) {
        return interviewService.generateQuestions(request.technologies());
    }

    /**
     * Creates a new interview.
     *
     * @param requestDto The interview data from the request body.
     * @return A ResponseEntity with status 201 (Created) and the created interview in the body.
     */
    @PostMapping
    public ResponseEntity<InterviewResponseDto> createInterview(@RequestBody InterviewRequestDto requestDto) {
        InterviewResponseDto createdInterview = interviewService.createInterview(requestDto);

        // Build the location URI of the newly created resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdInterview.interviewId()).toUri();

        return ResponseEntity.created(location).body(createdInterview);
    }
}