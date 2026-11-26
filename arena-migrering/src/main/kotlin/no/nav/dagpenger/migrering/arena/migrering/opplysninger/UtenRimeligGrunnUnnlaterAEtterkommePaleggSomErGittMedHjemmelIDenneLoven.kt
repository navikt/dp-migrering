package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: har ikke?
class UtenRimeligGrunnUnnlaterAEtterkommePaleggSomErGittMedHjemmelIDenneLoven : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    override val opplysningsId: UUID = UUID.fromString("019bc5f8-c377-740f-9df6-3c743b5144b1")
    override val opplysningsNavn: String =
        "Uten rimelig grunn unnlater å etterkomme pålegg som er gitt med hjemmel i denne loven"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = false,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
