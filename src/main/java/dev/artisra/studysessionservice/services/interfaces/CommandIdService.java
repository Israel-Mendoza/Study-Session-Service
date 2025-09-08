package dev.artisra.studysessionservice.services.interfaces;

public interface CommandIdService {
    boolean isProcessed(String commandId);
    void markAsProcessed(String commandId);
}
