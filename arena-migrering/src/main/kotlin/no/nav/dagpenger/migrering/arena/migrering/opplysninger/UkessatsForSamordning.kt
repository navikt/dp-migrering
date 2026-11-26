package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.util.UUID

// Arena: vedtakfakta UKESFSAM
class UkessatsForSamordning : PengeOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a240")
    override val opplysningsNavn: String = "Ukessats med barnetillegg før samordning"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BigDecimal> {
        val vedtakfakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { vedtakfakta -> vedtakfakta.kode == "UKESFSAM" }

        if (vedtakfakta == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vedtakfakta?.verdi),
            gyldigFraOgMed = vedtakfakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): BigDecimal? = verdi?.toBigDecimal()
}
