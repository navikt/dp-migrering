create table if not exists migrering_hendelse
(
    id                  BIGSERIAL PRIMARY KEY,
    ident               TEXT                                   NOT NULL,
    migrering_id        uuid                                   NOT NULL REFERENCES migrering (migrering_id),
    melding_id          uuid                                   NOT NULL UNIQUE,
    melding_type        TEXT                                   NOT NULL,
    data                jsonb                                  NOT NULL,
    lest_dato           TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

create index if not exists idx_migrering_hendelse_ident on migrering_hendelse (ident);

