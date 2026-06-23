package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaSakDetaljerDTO
import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaSakMedVedtak(
    val sakId: String,
    val opprettetAar: String,
    val lopenr: Int,
    val person: ArenaSakPerson,
    val statuskode: String,
    val statusnavn: String,
    val registrertDato: LocalDateTime,
    val avsluttetDato: LocalDateTime?,
    val vedtak: List<ArenaVedtakMedDetaljer>,
) {
    fun tilKontrakt(
        telleverkForPerson: TelleverkForPerson?,
        kvoteHistorikk: Set<KvotebrukHendelse>,
        sisteUtbetalingDato: LocalDate?,
        maksdato: LocalDate?,
    ) = ArenaSakDetaljerDTO(
        sakId = sakId,
        opprettetAar = opprettetAar,
        lopenr = lopenr,
        person = person.tilKontrakt(),
        statuskode = statuskode,
        statusnavn = statusnavn,
        registrertDato = registrertDato,
        avsluttetDato = avsluttetDato,
        vedtak = vedtak.map { it.tilKontrakt() },
        telleverkForPerson = telleverkForPerson?.tilKontrakt(),
        kvoteHistorikk = kvoteHistorikk.map { it.tilKontrakt() },
        maksdato = maksdato,
        sisteUtbetalingDato = sisteUtbetalingDato,
    )
}
