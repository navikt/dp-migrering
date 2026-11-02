package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class GrunnlagetForVernepliktErHoyereEnnDagpengegrunnlaget : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java
    override val opplysningsId: UUID = UUID.fromString("0194881f-9421-766c-9dc6-41fe6c9a1e05")
    override val opplysningsNavn: String = "Grunnlaget for verneplikt er høyere enn dagpengegrunnlaget"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
