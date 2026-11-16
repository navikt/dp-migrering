package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår GEOMOB
class KanJobbeIHeleNorge : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877bd9")
    override val opplysningsNavn: String = "Kan jobbe i hele Norge"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = true,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
