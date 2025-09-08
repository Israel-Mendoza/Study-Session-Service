package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.exceptions.AlreadyProcessedCommandException;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;
import dev.artisra.studysessionservice.models.exceptions.GeneralException;
import dev.artisra.studysessionservice.services.interfaces.CommandIdService;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sessions")
@Tag(name = "Study Session Service", description = "Endpoints creating study sessions, and manage active ones")
public class StudySessionController {

    private static final Logger logger = LoggerFactory.getLogger(StudySessionController.class);
    private final CommandIdService commandIdService;
    private final StudySessionService studySessionService;

    public StudySessionController(@Autowired CommandIdService commandIdService, @Autowired StudySessionService studySessionService) {
        this.commandIdService = commandIdService;
        this.studySessionService = studySessionService;
    }

    @PostMapping("/")
    @Operation(summary = "Create a new Study Session",
            description = "Creates a new study session - Will return 202 for non processed requests")
    @ApiResponse(responseCode = "202", description = "Study Session creation request successfully processed")
    @ApiResponse(responseCode = "208", description = "Command ID has already been processed", content =  @Content(schema = @Schema(implementation = GeneralException.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content =  @Content(schema = @Schema(implementation = GeneralException.class)))
    public ResponseEntity<Long> createStudySession(@Valid @RequestBody NewStudySessionRequest newStudySessionRequest) {
        logger.info("Received request to create study session: {}", newStudySessionRequest);
        checkIfCommandIsProcessed(newStudySessionRequest.commandId());
        studySessionService.createStudySession(newStudySessionRequest);
        commandIdService.markAsProcessed(newStudySessionRequest.commandId());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/commands")
    @Operation(summary = "Operates existing Study Sessions",
            description = "Sends commands to downstream services with any available action on an active Study Session")
    @ApiResponse(responseCode = "202", description = "Command on an active study session was processed successfully")
    @ApiResponse(responseCode = "208", description = "Command ID has already been processed", content =  @Content(schema = @Schema(implementation = GeneralException.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content =  @Content(schema = @Schema(implementation = GeneralException.class)))
    public ResponseEntity<Void> sendCommand(@PathVariable long sessionId, @Valid @RequestBody CommandRequest command) {
        logger.info("Received command for session ID {}: {}", sessionId, command);
        checkIfCommandIsProcessed(command.id());
        studySessionService.sendCommand(sessionId, command);
        commandIdService.markAsProcessed(command.id());
        return ResponseEntity.accepted().build();
    }

    private void checkIfCommandIsProcessed(String commandId) {
        if (commandIdService.isProcessed(commandId)) {
            logger.warn("Command with id {} is already processed", commandId);
            throw new AlreadyProcessedCommandException("Command with id " + commandId + " is already processed");
        }
    }
}
