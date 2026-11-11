package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: formidling ARBS edge-case hvis bruker på tiltak og ikke er arbeidssøker
class RegistrertSomArbeidssoker : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877be0")
    override val opplysningsNavn: String = "Registrert som arbeidssøker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning = kontekst.sakTilMigrering.personopplysninger.firstOrNull { it.kode == "FORMIDLINGSGRUPPEKODE" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(opplysning?.verdi),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "ARBS" -> {
                true
            }
            else -> {
                false
            }
        }
}
