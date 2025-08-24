package dev.artisra.studysessionservice.services.interfaces;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;

import java.util.List;

public interface StudySessionService {
    long createStudySession(StudySessionRequest studySessionRequest);

    void sendCommand(long sessionId, CommandRequest command);

    boolean exists(long sessionId);

    ActiveStudySession getActiveStudySession(long sessionId);

    List<ActiveStudySession> getAllStudySessions();

}
