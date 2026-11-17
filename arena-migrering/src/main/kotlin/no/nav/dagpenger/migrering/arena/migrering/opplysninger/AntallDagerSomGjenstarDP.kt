package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk DPTEL
class AntallDagerSomGjenstarDP : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01992956-e349-76b1-8f68-c9d481df3a32")
    override val opplysningsNavn: String = "Antall dager som gjenstår"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysning = kontekst.sakTilMigrering.gjenstaendeTeller.firstOrNull { it.kode == "GJENNDPTEL" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = (opplysning?.verdi?.toInt() ?: 0).div(20),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
