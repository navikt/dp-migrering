package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: SANKLENGDE?
class AntallUkerMedTidsbegrensetBortfall : HeltallsOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019e8300-20f7-7cba-b8dc-7bebfd86dcf3")
    override val opplysningsNavn: String = "Antall uker med tidsbegrenset bortfall"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "SANKLENGDE" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(fakta?.verdi) ?: 0,
            gyldigFraOgMed = fakta?.gyldigFra ?: kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
