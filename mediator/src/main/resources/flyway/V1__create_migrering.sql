create table if not exists migrering
(
    id           BIGSERIAL PRIMARY KEY,
    ident        TEXT  NOT NULL,
    melding_id   uuid  NOT NULL UNIQUE,
    melding_type TEXT  NOT NULL,
    data         jsonb NOT NULL,
);