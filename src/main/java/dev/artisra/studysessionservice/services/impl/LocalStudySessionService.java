package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.models.enums.StudySessionState;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Profile("dev")
public class LocalStudySessionService implements StudySessionService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStudySessionService.class);

    private final Map<Long, ActiveStudySession> activeStudySessions = new ConcurrentHashMap<>();

    private final AtomicLong lastId = new AtomicLong(0L);

    @Override
    public long createStudySession(StudySessionRequest studySessionRequest) {
        long id = lastId.incrementAndGet();
        ActiveStudySession newActiveStudySession = new ActiveStudySession(
                id,
                studySessionRequest.getSubject(),
                studySessionRequest.getTopic(),
                studySessionRequest.getUserId(),
                StudySessionState.NOT_STARTED);
        activeStudySessions.put(id, newActiveStudySession);
        return id;
    }

    @Override
    public ActiveStudySession getActiveStudySession(long sessionId) {
        return Optional.ofNullable(activeStudySessions.get(sessionId)).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("No active study session found with ID %d", sessionId)));
    }

    @Override
    public void sendCommand(long sessionId, CommandRequest command) {
        switch (command.getCommand()) {
            case START -> startStudySession(sessionId);
            case PAUSE -> pauseStudySession(sessionId);
            case RESUME -> resumeStudySession(sessionId);
            case COMPLETE -> completeStudySession(sessionId);
            case CANCEL -> cancelStudySession(sessionId);
            case DELETE -> deleteStudySession(sessionId);
        }
    }

    @Override
    public List<ActiveStudySession> getAllStudySessions() {
        return new ArrayList<>(activeStudySessions.values());
    }

    @Override
    public boolean exists(long sessionId) {
        return activeStudySessions.containsKey(sessionId);
    }

    private void startStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.NOT_STARTED) {
            logger.info("Study session with ID {} cannot be completed. State: {}", sessionId,
                    activeStudySession.getState());
            return;
        }
        activeStudySession.setState(StudySessionState.IN_PROGRESS);
    }

    private void pauseStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.IN_PROGRESS) {
            logger.info("Study session with ID {} cannot be paused. State: {}", sessionId,
                    activeStudySession.getState());
            return;
        }
        activeStudySession.setState(StudySessionState.PAUSED);
    }

    private void resumeStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() != StudySessionState.PAUSED) {
            logger.info("Study session with ID {} cannot be resumed. State: {}", sessionId,
                    activeStudySession.getState());
            return;
        }
        activeStudySession.setState(StudySessionState.IN_PROGRESS);
    }

    private void completeStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        StudySessionState state = activeStudySession.getState();
        if (state != StudySessionState.IN_PROGRESS && state != StudySessionState.PAUSED) {
            logger.info("Study session with ID {} cannot be completed. State: {}", sessionId, state);
            return;
        }
        activeStudySession.setState(StudySessionState.COMPLETED);
    }

    private void cancelStudySession(long sessionId) {
        ActiveStudySession activeStudySession = getActiveStudySession(sessionId);
        if (activeStudySession.getState() == StudySessionState.CANCELLED ||
                activeStudySession.getState() == StudySessionState.COMPLETED) {
            logger.info("Study session with ID {} cannot be cancelled. State: {}", sessionId,
                    activeStudySession.getState());
            return;
        }
        activeStudySession.setState(StudySessionState.CANCELLED);
    }

    private void deleteStudySession(long sessionId) {
        activeStudySessions.remove(sessionId);
    }
}
