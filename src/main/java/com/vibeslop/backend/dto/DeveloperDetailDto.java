package com.vibeslop.backend.dto;

import java.util.Set;

/**
 * A detailed representation of a Developer for API responses.
 *
 * @param id          The developer's unique ID.
 * @param name        The developer's name.
 * @param skills      A set of the developer's skills.
 * @param interviews  A set of summaries for the developer's interviews.
 */
public record DeveloperDetailDto(
        Long id,
        String name,
        Set<String> skills,
        Set<InterviewSummaryDto> interviews
) {}