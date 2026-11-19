package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Arena har ikke denne
class SoknadId : OpplysningProdusent<String> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-91d1-7df2-ba1d-4533f37fcc77")
    override val opplysningsNavn: String = "søknadId"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = UUID.randomUUID().toString(),
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
