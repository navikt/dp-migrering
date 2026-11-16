package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: sjekk
class SvangerskapspengerDagsats : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9433-70e9-a85b-c246150c45da")
    override val opplysningsNavn: String = "Svangerskapspenger dagsats"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 0,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
