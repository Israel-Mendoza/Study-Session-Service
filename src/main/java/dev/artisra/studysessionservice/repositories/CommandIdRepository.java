package dev.artisra.studysessionservice.repositories;

import dev.artisra.studysessionservice.entities.CommandIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandIdRepository extends JpaRepository<CommandIdEntity, String> {
    boolean existsByCommandId(String commandId);
}