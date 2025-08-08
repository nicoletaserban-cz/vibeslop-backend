package com.vibeslop.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record InterviewFeedbackRequest(@NotBlank String interviewTranscript) {}