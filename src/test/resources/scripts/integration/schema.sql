CREATE TABLE IF NOT EXISTS processed_commands
(
    command_id   VARCHAR(36) NOT NULL PRIMARY KEY,
    processed_at TIMESTAMP   NOT NULL
);