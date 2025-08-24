package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/sessions")
public class StudyServiceController {

    private final StudySessionService studySessionService;

    public StudyServiceController(@Autowired StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping("/new")
    public ResponseEntity<Long> createStudySession(@RequestBody StudySessionRequest studySessionRequest) {
        long newId = studySessionService.createStudySession(studySessionRequest);
        return ResponseEntity
                .created(URI.create("/" + newId))
                .body(newId);
    }

    @PostMapping("/{sessionId}/command")
    public ResponseEntity<Void> sendCommand(@PathVariable long sessionId, @RequestBody CommandRequest command) {
        if (!studySessionService.exists(sessionId)) {
            return ResponseEntity.notFound().build();
        }

        studySessionService.sendCommand(sessionId, command);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ActiveStudySession> getActiveStudySessionById(@PathVariable long sessionId) {
        if (!studySessionService.exists(sessionId)) {
            return ResponseEntity.notFound().build();
        }
        ActiveStudySession activeStudySession = studySessionService.getActiveStudySession(sessionId);
        return ResponseEntity.ok(activeStudySession);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveStudySession>> getActiveStudySessions() {
        return ResponseEntity.ok(studySessionService.getAllStudySessions());
    }
}
