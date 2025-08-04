package com.vibeslop.backend.dto;

/**
 * A lightweight summary of an interview, suitable for embedding in other DTOs.
 *
 * @param interviewId The ID of the interview.
 * @param clientName  The name of the client who conducted the interview.
 * @param feedback    The feedback provided during the interview.
 */
public record InterviewSummaryDto(
        Long interviewId,
        String clientName,
        String feedback
) {}