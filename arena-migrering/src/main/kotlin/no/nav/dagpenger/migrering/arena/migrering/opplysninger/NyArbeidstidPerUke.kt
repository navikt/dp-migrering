package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Vedtakfakta NAARBTID
class NyArbeidstidPerUke : OpplysningProdusent<Double> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a76b")
    override val opplysningsNavn: String = "Ny arbeidstid per uke"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
