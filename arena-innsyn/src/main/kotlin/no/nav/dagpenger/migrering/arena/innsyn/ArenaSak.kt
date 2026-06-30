package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaSakDTO
import java.sql.ResultSet
import java.time.LocalDateTime

data class ArenaSak(
    val sakId: String,
    val opprettetAar: String,
    val lopenr: Int,
    val person: ArenaSakPerson,
    val statuskode: String,
    val statusnavn: String,
    val registrertDato: LocalDateTime,
    val avsluttetDato: LocalDateTime?,
) {
    companion object {
        fun fraResultSet(row: ResultSet): ArenaSak =
            ArenaSak(
                sakId = row.getInt("sak_id").toString(),
                opprettetAar = row.getInt("aar").toString(),
                lopenr = row.getInt("lopenrsak"),
                statuskode = row.getString("sakstatuskode"),
                statusnavn = row.getString("sakstatusnavn"),
                registrertDato = row.getTimestamp("reg_dato").toLocalDateTime(),
                avsluttetDato = row.getTimestamp("dato_avsluttet")?.toLocalDateTime(),
                person =
                    ArenaSakPerson(
                        personId = row.getInt("person_id"),
                        fodselsnummer = row.getString("fodselsnr"),
                        fornavn = row.getString("fornavn"),
                        etternavn = row.getString("etternavn"),
                    ),
            )
    }

    fun tilArenaSakMedVedtak(vedtak: List<ArenaVedtakMedDetaljer>) =
        ArenaSakMedVedtak(
            sakId = sakId,
            opprettetAar = opprettetAar,
            lopenr = lopenr,
            person = person,
            registrertDato = registrertDato,
            avsluttetDato = avsluttetDato,
            statuskode = statuskode,
            statusnavn = statusnavn,
            vedtak = vedtak,
        )

    fun tilKontrakt() =
        ArenaSakDTO(
            sakId = sakId,
            opprettetAar = opprettetAar,
            lopenr = lopenr,
            registrertDato = registrertDato,
            avsluttetDato = avsluttetDato,
            statuskode = statuskode,
            statusnavn = statusnavn,
        )
}
