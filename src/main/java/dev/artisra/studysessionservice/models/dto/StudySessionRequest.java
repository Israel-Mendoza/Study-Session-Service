package dev.artisra.studysessionservice.models.dto;

public class StudySessionRequest {
    private String userId;
    private String subject;
    private String topic;

    public StudySessionRequest() {
    }

    public StudySessionRequest(String userId, String subject, String topic) {
        this.userId = userId;
        this.subject = subject;
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
