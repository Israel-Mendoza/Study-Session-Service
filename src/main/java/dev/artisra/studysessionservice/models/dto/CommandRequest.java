package dev.artisra.studysessionservice.models.dto;

import dev.artisra.studysessionservice.models.enums.StudySessionCommand;

import java.time.Instant;

public record CommandRequest(
        String commandId,                  // for idempotency
        StudySessionCommand command,
        String issuedBy,                   // userId or system
        Instant issuedAt                   // timestamp
) {
}
