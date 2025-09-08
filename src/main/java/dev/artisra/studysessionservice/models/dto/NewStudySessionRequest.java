package dev.artisra.studysessionservice.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record NewStudySessionRequest(
        @Schema(description = "Unique command ID (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
        @Size(min = 36, max = 36, message = "Command ID must be a valid UUID")
        String commandId,
        @Schema(description = "Integer representing the User ID", example = "2250")
        @Min(value = 0, message = "User ID must be a positive integer")
        int userId,
        @Schema(description = "Integer representing the Subject ID", example = "22256")
        @Min(value = 0, message = "Subject ID must be a positive integer")
        int subjectId,
        @Schema(description = "Integer representing the Topic ID", example = "22256556")
        @Min(value = 0, message = "Topic ID must be a positive integer")
        int topicId
) {
}
