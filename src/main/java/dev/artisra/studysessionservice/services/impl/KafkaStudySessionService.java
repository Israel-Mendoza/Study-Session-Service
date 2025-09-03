package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.dto.NewStudySessionRequest;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaStudySessionService implements StudySessionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${kafka.topic.study-sessions}")
    private String studySessionsTopic;
    @Value("${kafka.topic.commands}")
    private String commandsTopic;

    public KafkaStudySessionService(@Autowired KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public long createStudySession(NewStudySessionRequest newStudySessionRequest) {
        // Generate a unique session ID
        long sessionId = System.currentTimeMillis();
        // Send the study session request to Kafka
        kafkaTemplate.send(studySessionsTopic, String.valueOf(sessionId), newStudySessionRequest.toString());
        return sessionId;
    }

    @Override
    public void sendCommand(long sessionId, CommandRequest command) {
        kafkaTemplate.send(commandsTopic, String.valueOf(sessionId), command.toString());
    }
}
