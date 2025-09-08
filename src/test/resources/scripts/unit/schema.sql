create table if not exists processed_commands
(
    command_id   varchar(36) not null primary key,
    processed_at timestamp   not null
);