package no.nav.dagpenger.migrering.arena.innsyn

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
}
