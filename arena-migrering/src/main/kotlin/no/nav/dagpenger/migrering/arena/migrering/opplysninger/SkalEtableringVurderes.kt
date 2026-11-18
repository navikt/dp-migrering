package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår? aktfase UETO/UETA?
class SkalEtableringVurderes : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01a0c316-a2e9-7088-acb1-da5596bd0055")
    override val opplysningsNavn: String = "Skal etablering vurderes"

// Bare lagt til midlertidig FALSE
    // IKKE OK!
    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning = kontekst.sakTilMigrering.aktfase.firstOrNull { aktfase -> aktfase.kode == "AKTFASE" }

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
            in setOf("UETO", "UETA", "UETU") -> true
            else -> false
        }
}
