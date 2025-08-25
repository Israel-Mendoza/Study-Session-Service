package dev.artisra.studysessionservice.clients;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ActiveStudySessionClient {
    private final RestTemplate restTemplate;

    @Value("${study.session.service.url}")
    private String studySessionServiceUrl;

    public ActiveStudySessionClient(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean doesActiveSessionExist(long sessionId) {
        // TODO: Implement the logic to call the external service's existence endpoint
        return false;
    }

    public ActiveStudySession getActiveStudySession(long sessionId) {
        // TODO: Implement the logic to call the external service's fetch endpoint
        return null;
    }

    public List<ActiveStudySession> getAllActiveStudySessions() {
        // TODO: Implement the logic to call the external service's fetch all endpoint
        return List.of();
    }
}
