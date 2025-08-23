package dev.artisra.studysessionservice.services.interfaces;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;

import java.util.List;

public interface StudySessionService {
    long createStudySession(StudySessionRequest studySessionRequest);

    ActiveStudySession getActiveStudySession(long sessionId);

    void startStudySession(long sessionId);

    void pauseStudySession(long sessionId);

    void resumeStudySession(long sessionId);

    void completeStudySession(long sessionId);

    void cancelStudySession(long sessionId);

    void deleteStudySession(long sessionId);

    List<ActiveStudySession> getAllStudySessions();
}
