package dev.artisra.studysessionservice.models.dto;

import java.util.Date;

public class StudySessionDetails {
    private long id;
    private String subject;
    private String topic;
    private Date startTime;
    private int duration;

    public StudySessionDetails() {
    }

    public StudySessionDetails(long id, String subject, String topic, Date startTime, int duration) {
        this.id = id;
        this.subject = subject;
        this.topic = topic;
        this.startTime = startTime;
        this.duration = duration;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
