package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class GrunnlagetForVernepliktErHoyereEnnDagpengegrunnlaget : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy
    override val opplysningsId: UUID = UUID.fromString("0194881f-9421-766c-9dc6-41fe6c9a1e05")
    override val opplysningsNavn: String = "Grunnlaget for verneplikt er høyere enn dagpengegrunnlaget"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = false,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
