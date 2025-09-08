package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.entities.CommandIdEntity;
import dev.artisra.studysessionservice.repositories.CommandIdRepository;
import dev.artisra.studysessionservice.services.interfaces.CommandIdService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommandIdServiceImplTest {

    @MockitoBean
    private CommandIdRepository commandIdRepository;

    @Autowired
    private CommandIdService commandIdService;

    @Test
    void isProcessed_ReturnsTrue() {
        String processedCommandID = "db3b6a1c-56cf-430b-8de4-4f2724b9c4a6";
        when(commandIdRepository.existsByCommandId(processedCommandID)).thenReturn(true);

        assertTrue(commandIdService.isProcessed(processedCommandID));
    }

    @Test
    void isProcessed_ReturnsFalse() {
        String unprocessedCommandID = "35352b21-248f-40c0-9db0-3bc11901cea5";
        when(commandIdRepository.existsByCommandId(unprocessedCommandID)).thenReturn(false);

        assertFalse(commandIdService.isProcessed(unprocessedCommandID));
    }

    @Test
    void markAsProcessed_shouldSaveNewCommandId() {
        // Arrange
        String commandId = "e35ae50e-ee7d-43b2-8ac2-06dcefdf1241";
        when(commandIdRepository.existsByCommandId(commandId)).thenReturn(false);

        // Act
        commandIdService.markAsProcessed(commandId);

        // Assert
        ArgumentCaptor<CommandIdEntity> commandIdCaptor = ArgumentCaptor.forClass(CommandIdEntity.class);
        verify(commandIdRepository, times(1)).save(commandIdCaptor.capture());

        CommandIdEntity savedEntity = commandIdCaptor.getValue();
        assertThat(savedEntity.getCommandId()).isEqualTo(commandId);
        assertThat(savedEntity.getProcessedAt()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        // Verify state is now "processed"
        when(commandIdRepository.existsByCommandId(commandId)).thenReturn(true);
        assertTrue(commandIdService.isProcessed(commandId));
    }
}