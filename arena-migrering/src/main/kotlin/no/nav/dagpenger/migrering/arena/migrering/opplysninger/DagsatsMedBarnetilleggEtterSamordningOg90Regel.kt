package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal

class DagsatsMedBarnetilleggEtterSamordningOg90Regel : PengeOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f")
    override val opplysningsNavn: String = "Dagsats med barnetillegg etter samordning og 90 % regel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BigDecimal> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { vedtakfakta -> vedtakfakta.kode == "DAGS" }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = vedtakfakta?.verdi?.toBigDecimal(),
            gyldigFraOgMed = vedtakfakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
