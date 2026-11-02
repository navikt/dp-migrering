create table if not exists migrering
(
    migrering_id uuid    NOT NULL PRIMARY KEY,
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

create table if not exists migrering_opplysning
(
    id           BIGSERIAL PRIMARY KEY,
    migrering_id uuid NOT NULL,
    opplysning_id uuid NOT NULL,
    datatype          TEXT,
    verdi_heltall     INT,
    verdi_desimaltall DECIMAL,
    verdi_dato        DATE,
    verdi_boolsk      BOOLEAN,
    verdi_string      TEXT,
    CONSTRAINT migrering_opplysning_unik_kobling UNIQUE (migrering_id, opplysning_id)
);

