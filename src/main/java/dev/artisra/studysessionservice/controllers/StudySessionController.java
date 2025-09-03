package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/sessions")
public class StudySessionController {

    private final StudySessionService studySessionService;

    private static final Logger logger = LoggerFactory.getLogger(StudySessionController.class);

    public StudySessionController(@Autowired StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping("/")
    public ResponseEntity<Long> createStudySession(@Valid @RequestBody NewStudySessionRequest newStudySessionRequest) {
        logger.info("Received request to create study session: {}", newStudySessionRequest);
        long newId = studySessionService.createStudySession(newStudySessionRequest);
        return ResponseEntity
                .created(URI.create("api/sessions/" + newId))
                .body(newId);
    }

    @PostMapping("/{sessionId}/commands")
    public ResponseEntity<Void> sendCommand(@PathVariable long sessionId, @RequestBody CommandRequest command) {
        logger.info("Received command for session {}: {}", sessionId, command);
        studySessionService.sendCommand(sessionId, command);
        return ResponseEntity.accepted().build();
    }
}
