package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.util.UUID

// Arena: sjekk
class PensjonFraEnOffentligTjenestepensjonsordningBelop : PengeOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    override val opplysningsId: UUID = UUID.fromString("0194881f-942e-7cb0-aa59-05ea449d88e7")
    override val opplysningsNavn: String = "Pensjon fra en offentlig tjenestepensjonsordning beløp"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BigDecimal> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = BigDecimal.ZERO,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
