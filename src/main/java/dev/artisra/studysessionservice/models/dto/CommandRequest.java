package dev.artisra.studysessionservice.models.dto;

import dev.artisra.studysessionservice.config.EnumValidator;
import dev.artisra.studysessionservice.models.enums.StudySessionCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CommandRequest(
        @Schema(description = "Unique command ID (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
        @Size(min = 36, max = 36, message = "Command ID must be a valid UUID")
        String id,
        @Schema(
                description = "Available commands on a Study Session [START, PAUSE, RESUME, COMPLETE, CANCEL, DELETE]",
                example = "START"
        )
        @NotNull(message = "Command cannot be null")
        @EnumValidator(enumClass = StudySessionCommand.class, message = "Invalid command value. Must be one of: START, PAUSE, RESUME, COMPLETE, CANCEL, or DELETE")
        String command,
        @Schema(description = "The issuer of the command [username or system]", example = "system")
        @NotNull(message = "Issued By cannot be null")
        @Size(min = 6, max = 50, message = "Issued By must be between 6 and 50 characters")
        String issuedBy
) {
}
