package com.vibeslop.backend.dto;

import java.time.LocalDateTime;

/**
 * A standard record for returning consistent JSON error responses.
 *
 * @param timestamp The time the error occurred.
 * @param status    The HTTP status code.
 * @param error     The HTTP status message.
 * @param message   A developer-friendly error message.
 * @param path      The path where the error occurred.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}