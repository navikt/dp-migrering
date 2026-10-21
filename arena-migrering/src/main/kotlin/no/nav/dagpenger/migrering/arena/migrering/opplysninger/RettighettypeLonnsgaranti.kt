package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class RettighettypeLonnsgaranti : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9444-7a73-a458-0af81c034d87")
    val opplysningsNavn: String = "Forskutterte lønnsgarantimidler i form av dagpenger skal vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val rettighetstype =
            kontekst.sakTilMigrering.rettighetstype.filter { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }
        if (rettighetstype.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(rettighetstype.first().verdi),
                gyldigFraOgMed = rettighetstype.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne om Forskutterte lønnsgarantimidler i form av dagpenger skal vurderes")
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "LONN" -> {
                true
            }

            else -> {
                false
            }
        }
}
