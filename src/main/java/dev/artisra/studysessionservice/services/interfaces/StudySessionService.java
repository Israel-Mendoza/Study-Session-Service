package dev.artisra.studysessionservice.services.interfaces;

import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;

public interface StudySessionService {
    void createStudySession(NewStudySessionRequest newStudySessionRequest);

    void sendCommand(long sessionId, CommandRequest command);
}
