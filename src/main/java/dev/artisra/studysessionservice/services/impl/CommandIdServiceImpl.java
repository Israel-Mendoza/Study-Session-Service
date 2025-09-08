package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.entities.CommandIdEntity;
import dev.artisra.studysessionservice.repositories.CommandIdRepository;
import dev.artisra.studysessionservice.services.interfaces.CommandIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommandIdServiceImpl implements CommandIdService {

    private final CommandIdRepository commandIdRepository;

    public CommandIdServiceImpl(@Autowired CommandIdRepository commandIdRepository) {
        this.commandIdRepository = commandIdRepository;
    }

    @Override
    public boolean isProcessed(String commandId) {
        return commandIdRepository.existsByCommandId(commandId);
    }

    @Override
    public void markAsProcessed(String commandId) {
        CommandIdEntity commandIdEntity = new CommandIdEntity();
        commandIdEntity.setCommandId(commandId);
        commandIdEntity.setProcessedAt(LocalDateTime.now());
        commandIdRepository.save(commandIdEntity);
    }
}
