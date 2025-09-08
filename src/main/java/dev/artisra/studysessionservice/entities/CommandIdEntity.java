package dev.artisra.studysessionservice.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_commands")
public class CommandIdEntity {

    @Id
    @Column(name = "command_id", length = 36, nullable = false)
    private String commandId;

    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

    // Getters and setters

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "CommandIdEntity(commandId= " +  commandId + ", processedAt= " + processedAt + ")";
    }
}