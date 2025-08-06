
package com.vibeslop.backend.dto;

import java.util.Set;

/**
 * A summary representation of a Developer, suitable for list views.
 *
 * @param id     The developer's unique ID.
 * @param name   The developer's name.
 * @param skills A set of the developer's skills.
 */
public record DeveloperSummaryDto(
        Long id,
        String name,
        Set<String> skills
) {}

