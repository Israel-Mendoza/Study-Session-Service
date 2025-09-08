package dev.artisra.studysessionservice.models.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public record GeneralException(
        LocalDateTime timestamp,
        @Schema(description = "The status code", example = "208")
        int statusCode,
        Map<String, String> cause,
        String description
) {
}
