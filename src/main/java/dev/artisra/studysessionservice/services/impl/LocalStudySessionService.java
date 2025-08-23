package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.models.enums.StudySessionState;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocalStudySessionService implements StudySessionService {

    List<ActiveStudySession> activeStudySessions = new ArrayList<>();
    long lastId = 0L;

    @Override
    public long createStudySession(StudySessionRequest studySessionRequest) {
        ActiveStudySession newActiveStudySession = new ActiveStudySession(
                ++lastId,
                studySessionRequest.getSubject(),
                studySessionRequest.getTopic(),
                studySessionRequest.getUserId(),
                StudySessionState.NOT_STARTED
        );
        activeStudySessions.add(newActiveStudySession);
        return lastId;
    }

    @Override
    public ActiveStudySession getActiveStudySession(long sessionId) {
        for (ActiveStudySession activeSession : activeStudySessions) {
            if (activeSession.getId() == sessionId) {
                return activeSession;
            }
        }
        throw new IllegalArgumentException("Active Study Session with ID" + sessionId + "not found");
    }

    @Override
    public void startStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.NOT_STARTED) {
            throw new IllegalStateException("Active Study Session with ID " + sessionId + " is already started or completed");
        }
        activeStudySession.setState(StudySessionState.IN_PROGRESS);
    }

    @Override
    public void pauseStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.IN_PROGRESS) {
            throw new IllegalStateException("Active Study Session with ID " + sessionId + " cannot be paused");
        }
        activeStudySession.setState(StudySessionState.PAUSED);
    }

    @Override
    public void resumeStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.PAUSED) {
            throw new IllegalStateException("Active Study Session with ID " + sessionId + " cannot be resumed");
        }
        activeStudySession.setState(StudySessionState.IN_PROGRESS);
    }

    @Override
    public void completeStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        StudySessionState state = activeStudySession.getState();
        if (state == StudySessionState.COMPLETED || state == StudySessionState.CANCELLED) {
            throw new IllegalStateException("Study Session with ID " + sessionId + " is either completed or cancelled");
        }
        activeStudySession.setState(StudySessionState.COMPLETED);
    }

    @Override
    public void cancelStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() == StudySessionState.CANCELLED) {
            throw new IllegalStateException("Study Session with ID " + sessionId + " has already been cancelled");
        }
        activeStudySession.setState(StudySessionState.CANCELLED);
    }

    @Override
    public void deleteStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        activeStudySessions.remove(activeStudySession);
    }

    @Override
    public List<ActiveStudySession> getAllStudySessions() {
        return activeStudySessions;
    }
}
