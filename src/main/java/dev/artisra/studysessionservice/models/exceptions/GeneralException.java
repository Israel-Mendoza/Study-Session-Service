package dev.artisra.studysessionservice.models.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public class GeneralException {
    private final LocalDateTime timestamp;
    private final int statusCode;
    private final Map<String, String> cause;
    private final String description;

    public GeneralException(LocalDateTime timestamp, int statusCode, Map<String, String> cause, String description) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.cause = cause;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getCause() {
        return cause;
    }

    public String getDescription() {
        return description;
    }
}
