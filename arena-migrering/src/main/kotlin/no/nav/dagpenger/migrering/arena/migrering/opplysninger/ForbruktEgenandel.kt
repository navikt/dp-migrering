package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.util.UUID

// Arena: kvotebruk EGENA
class ForbruktEgenandel : PengeOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy
    override val opplysningsId: UUID = UUID.fromString("01973a27-d8b3-7ffd-a81a-a3826963b079")
    override val opplysningsNavn: String = "Forbrukt egenandel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BigDecimal> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 25000.toBigDecimal(),
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
