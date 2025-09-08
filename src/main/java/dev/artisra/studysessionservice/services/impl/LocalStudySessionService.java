package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalStudySessionService implements StudySessionService {


    private static final Logger logger = LoggerFactory.getLogger(LocalStudySessionService.class);

    @Override
    public void createStudySession(NewStudySessionRequest newStudySessionRequest) {
        logger.info("Creating study session locally: {}", newStudySessionRequest);
    }

    @Override
    public void sendCommand(long sessionId, CommandRequest command) {
        logger.info("Sending command to session {} locally: {}", sessionId, command);
    }
}
