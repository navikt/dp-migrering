package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: ? Må ha... vedtakfakta NAARBTID?
class OnsketArbeidstid : OpplysningProdusent<Double> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a75f")
    override val opplysningsNavn: String = "Ønsket arbeidstid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 37.5,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
