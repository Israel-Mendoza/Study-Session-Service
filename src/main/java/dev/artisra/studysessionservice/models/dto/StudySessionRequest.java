package dev.artisra.studysessionservice.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudySessionRequest {
    @NotBlank(message = "User ID cannot be blank")
    @Size(min = 6, max = 50, message = "User ID must be between 6 and 50 characters")
    private String userId;
    @NotBlank(message = "Subject cannot be blank")
    @Size(min = 2, max = 100, message = "Subject must be between 2 and 100 characters")
    private String subject;
    @NotBlank(message = "Topic cannot be blank")
    @Size(min = 2, max = 100, message = "Subject must be between 2 and 100 characters")
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
