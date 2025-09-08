package dev.artisra.studysessionservice.repositories;

import dev.artisra.studysessionservice.entities.CommandIdEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@DataJpaTest
@ActiveProfiles("unit")
@Sql(scripts = {"/scripts/unit/schema.sql", "/scripts/unit/data.sql"})
@Sql(scripts = {"/scripts/unit/cleanup.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CommandIdRepositoryTest {

    @Autowired
    private CommandIdRepository commandIdRepository;

    @Test
    public void testExistsByCommandId_FalseForNonExistingUUID() {
        String commandId = "79a69eb1-9f73-44d6-869d-3b18ac5d50f4"; // Doesn't exist
        boolean exists = commandIdRepository.existsByCommandId(commandId);
        assertFalse(exists);
    }

    @Test
    public void testExistsByCommandId_TrueForExistingUUID() {
        String commandId = "29b6c1bb-e14d-4987-8ab8-a1b0352c82b8";
        boolean exists = commandIdRepository.existsByCommandId(commandId);
        assertTrue(exists);
    }

    @Test
    void testExistsByCommandIdAfterSave_shouldBeTrue() {
        String nonExistingUUID = "19e57c74-be9c-4212-8865-e105a075dc05";
        boolean exists = commandIdRepository.existsByCommandId(nonExistingUUID);
        assertFalse(exists);

        // Storing the UUID
        LocalDateTime now = LocalDateTime.now();
        CommandIdEntity commandIdEntity = new CommandIdEntity();
        commandIdEntity.setCommandId(nonExistingUUID);
        commandIdEntity.setProcessedAt(LocalDateTime.now());

        commandIdRepository.save(commandIdEntity);
        assertTrue(commandIdRepository.existsByCommandId(nonExistingUUID));

        CommandIdEntity storedCommandIdEntity = commandIdRepository.findById(nonExistingUUID).orElseThrow();

        // Assert that the processedAt timestamp is close to the time we saved it
        assertThat(storedCommandIdEntity.getProcessedAt())
                .isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }
}
