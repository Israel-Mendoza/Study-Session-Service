package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

    @PostMapping("/{sessionId}/start")
    public ResponseEntity<Void> startStudySession(@PathVariable long sessionId) {
        studySessionService.startStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/pause")
    public ResponseEntity<Void> pauseStudySession(@PathVariable long sessionId) {
        studySessionService.pauseStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/resume")
    public ResponseEntity<Void> resumeStudySession(@PathVariable long sessionId) {
        studySessionService.resumeStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<Void> completeStudySession(@PathVariable long sessionId) {
        studySessionService.completeStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/cancel")
    public ResponseEntity<Void> cancelStudySession(@PathVariable long sessionId) {
        studySessionService.cancelStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{sessionId}/delete")
    public ResponseEntity<Void> deleteStudySession(@PathVariable long sessionId) {
        studySessionService.deleteStudySession(sessionId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ActiveStudySession> getActiveStudySessionById(@PathVariable long sessionId) {
        ActiveStudySession activeStudySession = studySessionService.getActiveStudySession(sessionId);
        return ResponseEntity.ok(activeStudySession);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveStudySession>> getActiveStudySessions() {
        return ResponseEntity.ok(studySessionService.getAllStudySessions());
    }
}
