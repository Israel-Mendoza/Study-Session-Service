package dev.artisra.studysessionservice.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record NewStudySessionRequest(
        @Size(min = 36, max = 36, message = "Command ID must be a valid UUID")
        String commandId,
        @Min(value = 0, message = "User ID must be a positive integer")
        int userId,
        @Min(value = 0, message = "Subject ID must be a positive integer")
        int subjectId,
        @Min(value = 0, message = "Topic ID must be a positive integer")
        int topicId
) {
}
