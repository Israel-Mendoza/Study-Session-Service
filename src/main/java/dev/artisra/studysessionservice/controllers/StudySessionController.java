package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.exceptions.AlreadyProcessedCommandException;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.CommandIdService;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sessions")
public class StudySessionController {

    private static final Logger logger = LoggerFactory.getLogger(StudySessionController.class);
    private final CommandIdService commandIdService;
    private final StudySessionService studySessionService;

    public StudySessionController(@Autowired CommandIdService commandIdService, @Autowired StudySessionService studySessionService) {
        this.commandIdService = commandIdService;
        this.studySessionService = studySessionService;
    }

    @PostMapping("/")
    public ResponseEntity<Long> createStudySession(@Valid @RequestBody NewStudySessionRequest newStudySessionRequest) {
        logger.info("Received request to create study session: {}", newStudySessionRequest);
        checkIfCommandIsProcessed(newStudySessionRequest.commandId());
        studySessionService.createStudySession(newStudySessionRequest);
        commandIdService.markAsProcessed(newStudySessionRequest.commandId());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/commands")
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
