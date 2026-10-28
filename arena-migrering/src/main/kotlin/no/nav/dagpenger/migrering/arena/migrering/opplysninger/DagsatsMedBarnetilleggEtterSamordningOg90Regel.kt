package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

class DagsatsMedBarnetilleggEtterSamordningOg90Regel : OpplysningProdusent<Int> {
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f")
    override val opplysningsNavn: String = "Dagsats med barnetillegg etter samordning og 90 % regel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
