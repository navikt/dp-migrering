package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class IkkeSelvforskyldtArbeidsledig : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01a0c30f-af05-736e-8d44-bd525b24bf4a")
    override val opplysningsNavn: String = "Ikke selvforskyldt arbeidsledig"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
