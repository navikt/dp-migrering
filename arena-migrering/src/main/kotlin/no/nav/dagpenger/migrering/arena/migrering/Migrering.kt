package no.nav.dagpenger.migrering.arena.migrering

//     migrering_id uuid    NOT NULL,
//    ident        TEXT    NOT NULL,
//    sak_id       NUMERIC NOT NULL,
//    initiert_av  TEXT    NOT NULL,
//    opprettet    TIMESTAMP WITH TIME ZONE DEFAULT NOW()
import no.nav.dagpenger.uuid.UUIDv7
import java.time.LocalDateTime
import java.util.UUID

data class Migrering(
    val migreringId: UUID = UUIDv7.ny(),
    val ident: String,
    val sakId: Int,
    val initiertAv: String,
    val opprettet: LocalDateTime = LocalDateTime.now(),
    val simulering: Boolean = false,
)
