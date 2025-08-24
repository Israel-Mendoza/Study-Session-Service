package dev.artisra.studysessionservice.models.dto;

import dev.artisra.studysessionservice.models.enums.StudySessionCommand;

public class CommandRequest {
    private StudySessionCommand command;

    public CommandRequest() {
    }

    public CommandRequest(StudySessionCommand command) {
        this.command = command;
    }

    public StudySessionCommand getCommand() {
        return command;
    }
}
