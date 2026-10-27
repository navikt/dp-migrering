package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class HarGittFullstendigeOgRiktigeOpplysninger : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019bc5f8-fdf3-7240-afaf-52c0486eddc5")
    override val opplysningsNavn: String = "Har gitt fullstendige og riktige opplysninger"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
