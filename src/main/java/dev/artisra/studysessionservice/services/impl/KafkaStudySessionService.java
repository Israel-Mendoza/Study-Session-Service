package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.clients.ActiveStudySessionClient;
import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("prod")
public class KafkaStudySessionService implements StudySessionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ActiveStudySessionClient activeStudySessionClient;
    @Value("${kafka.topic.study-sessions}")
    private String studySessionsTopic;
    @Value("${kafka.topic.commands}")
    private String commandsTopic;

    public KafkaStudySessionService(@Autowired KafkaTemplate<String, String> kafkaTemplate, @Autowired ActiveStudySessionClient activeStudySessionClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.activeStudySessionClient = activeStudySessionClient;
    }

    @Override
    public long createStudySession(StudySessionRequest studySessionRequest) {
        // Generate a unique session ID
        long sessionId = System.currentTimeMillis();
        // Send the study session request to Kafka
        kafkaTemplate.send(studySessionsTopic, String.valueOf(sessionId), studySessionRequest.toString());
        return sessionId;
    }

    @Override
    public void sendCommand(long sessionId, CommandRequest command) {
        kafkaTemplate.send(commandsTopic, String.valueOf(sessionId), command.toString());
    }

    @Override
    public boolean exists(long sessionId) {
        return activeStudySessionClient.doesActiveSessionExist(sessionId);
    }

    @Override
    public ActiveStudySession getActiveStudySession(long sessionId) {
        return activeStudySessionClient.getActiveStudySession(sessionId);
    }

    @Override
    public List<ActiveStudySession> getAllStudySessions() {
        return activeStudySessionClient.getAllActiveStudySessions();
    }
}
