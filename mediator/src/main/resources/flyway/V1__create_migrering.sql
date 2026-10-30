create table if not exists migrering
(
    migrering_id uuid    NOT NULL,
    ident        TEXT    NOT NULL,
    sak_id       NUMERIC NOT NULL,
    initiert_av  TEXT    NOT NULL,
    opprettet    TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists migrering_hendelse
(
    id           BIGSERIAL PRIMARY KEY,
    migrering_id uuid  NOT NULL,
    melding_id   uuid  NOT NULL UNIQUE,
    melding_type TEXT  NOT NULL,
    melding      jsonb NOT NULL
);

