package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

// Arena: dp-behandling setter dette fra migreringsdato
class Provingsdato : OpplysningProdusent<LocalDate> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false
    override val opplysningsType: Class<LocalDate> = LocalDate::class.java
    override val opplysningsId: UUID = UUID.fromString("0194881f-91d1-7df2-ba1d-4533f37fcc76")
    override val opplysningsNavn: String = "Prøvingsdato"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
