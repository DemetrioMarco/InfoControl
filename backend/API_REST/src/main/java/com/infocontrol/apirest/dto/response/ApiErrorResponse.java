package com.infocontrol.apirest.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public record ApiErrorResponse(
        int status,
        String message,
        String timestamp,
        String path,
        Map<String, String> errors
) {

    /**
     * Constructor sin path (mantiene compatibilidad con código existente)
     */
    public ApiErrorResponse(int status, String message, Map<String, String> errors) {
        this(
                status,
                message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                null,
                errors
        );
    }

    /**
     * Constructor con path
     */
    public ApiErrorResponse(int status, String message, String path, Map<String, String> errors) {
        this(
                status,
                message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                path,
                errors
        );
    }
}
