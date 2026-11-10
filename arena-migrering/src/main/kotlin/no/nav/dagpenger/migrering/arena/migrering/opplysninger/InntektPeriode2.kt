package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vedtakfakta INTNESTS
class InntektPeriode2 : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsType: Class<Int> = Int::class.java

    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cae")
    override val opplysningsNavn: String = "Utbetalt inntekt periode 2"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val vedtakfakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { vedtakfakta -> vedtakfakta.kode == "INTNESTS" }

        if (vedtakfakta == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vedtakfakta?.verdi),
            gyldigFraOgMed = vedtakfakta?.gyldigFra,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
