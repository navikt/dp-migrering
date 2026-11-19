package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk EGENA
class GjenstaendeEgenandel : OpplysningProdusent<Double> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01997b70-a12c-7622-bff8-82a20687e640")
    override val opplysningsNavn: String = "Gjenstående egenandel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> {
        val opplysning =
            kontekst.sakTilMigrering.gjenstaendeTeller.firstOrNull { it.kode == "GJENNTELLER" && it.kodetype == "EGENA" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = (opplysning?.verdi?.toDouble() ?: 0.0),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
