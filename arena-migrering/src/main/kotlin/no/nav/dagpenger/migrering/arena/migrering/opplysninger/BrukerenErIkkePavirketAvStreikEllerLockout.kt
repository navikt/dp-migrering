package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class BrukerenErIkkePavirketAvStreikEllerLockout : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-91df-746a-a8ac-4a6b2b30685f")
    override val opplysningsNavn: String = "Brukeren er ikke påvirket av streik eller lock-out"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean>? {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "OATVIST" }
                ?: return vedManglendeOpplysning()
        return Opplysning(
            navn = opplysningsNavn,
            verdi = true,
            gyldigFraOgMed = LocalDate.of(2018, 6, 21),
            uuid = opplysningsId,
        )
    }
}
