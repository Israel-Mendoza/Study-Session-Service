package dev.artisra.studysessionservice.models.dto;

import dev.artisra.studysessionservice.models.enums.StudySessionState;

public class ActiveStudySession {
    private long id;
    private String subject;
    private String topic;
    private String userId;
    private StudySessionState state;

    public ActiveStudySession() {
    }

    public ActiveStudySession(long id, String subject, String topic, String userId, StudySessionState state) {
        this.id = id;
        this.subject = subject;
        this.topic = topic;
        this.userId = userId;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public StudySessionState getState() {
        return state;
    }

    public void setState(StudySessionState state) {
        this.state = state;
    }
}
