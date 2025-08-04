package com.vibeslop.backend.dto;

/**
 * DTO for creating a new Interview.
 * This object is expected in the request body.
 *
 * @param developerId The ID of the developer being interviewed.
 * @param clientId    The ID of the client conducting the interview.
 * @param feedback    The feedback from the interview.
 */
public record InterviewRequestDto(
        Long developerId,
        Long clientId,
        String feedback
) {}
