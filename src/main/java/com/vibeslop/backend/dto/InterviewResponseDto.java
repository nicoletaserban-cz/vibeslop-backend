package com.vibeslop.backend.dto;

/**
 * A detailed DTO representing a created or retrieved Interview.
 *
 * @param interviewId   The ID of the interview.
 * @param developerId   The ID of the interviewed developer.
 * @param developerName The name of the interviewed developer.
 * @param clientId      The ID of the client.
 * @param clientName    The name of the client.
 * @param feedback      The feedback from the interview.
 */
public record InterviewResponseDto(
        Long interviewId,
        Long developerId, String developerName,
        Long clientId, String clientName,
        String feedback
) {}