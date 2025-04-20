create table IF NOT EXISTS Vulnerabilities (
    id int auto_increment primary key,
    link         longtext                              not null,
    processed_at timestamp default current_timestamp() not null
);

