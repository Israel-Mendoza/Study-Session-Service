package dev.artisra.studysessionservice.exceptions;

public class AlreadyProcessedCommandException extends RuntimeException {
    public AlreadyProcessedCommandException(String message) {
        super(message);
    }
}
