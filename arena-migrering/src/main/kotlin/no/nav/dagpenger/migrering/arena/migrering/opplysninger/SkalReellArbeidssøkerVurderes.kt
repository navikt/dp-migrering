package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class SkalReellArbeidssøkerVurderes : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Int> = Int::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194ff86-a035-7eb0-9c60-21899f7cc0c1")
    override val opplysningsNavn: String = "Skal reell arbeidssøker vurderes"

// Forventer at den skal være true ? Feil ordlyd
    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
