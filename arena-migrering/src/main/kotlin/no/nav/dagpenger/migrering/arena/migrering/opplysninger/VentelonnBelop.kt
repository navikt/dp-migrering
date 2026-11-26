package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.util.UUID

// Arena: sjekk
class VentelonnBelop : PengeOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-942f-7bde-ab16-68ffd19e9a27")
    override val opplysningsNavn: String = "Ventelønn beløp"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BigDecimal> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = BigDecimal.ZERO,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
