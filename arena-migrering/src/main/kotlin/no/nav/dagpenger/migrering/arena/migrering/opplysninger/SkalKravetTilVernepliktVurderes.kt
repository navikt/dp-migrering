package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class SkalKravetTilVernepliktVurderes : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("01980cf4-9010-7bcf-b578-ca5a825d64ef")
    override val opplysningsNavn: String = "Skal kravet til verneplikt vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
