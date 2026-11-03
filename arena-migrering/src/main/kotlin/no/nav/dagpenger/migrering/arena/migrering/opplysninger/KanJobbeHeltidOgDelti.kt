package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class KanJobbeHeltidOgDelti : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Int> = Int::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9441-7d1b-a06a-6727543a141e")
    override val opplysningsNavn: String = "Kan jobbe heltid og deltid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
