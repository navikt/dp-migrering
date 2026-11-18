package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår ARBFØR
class KanTaAlleTyperArbeid : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877bdc")
    override val opplysningsNavn: String = "Kan ta alle typer arbeid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.firstOrNull { vilkaar -> vilkaar.kode == "ARBFØR" }

        if (vilkaar == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vilkaar?.verdi),
            gyldigFraOgMed = vilkaar?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "J" -> {
                true
            }

            "N" -> {
                false
            }

            else -> {
                null
            }
        }
}
