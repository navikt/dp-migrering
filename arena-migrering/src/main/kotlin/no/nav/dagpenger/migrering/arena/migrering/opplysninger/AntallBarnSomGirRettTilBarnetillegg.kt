package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class AntallBarnSomGirRettTilBarnetillegg : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val opplysningsType: Class<Int> = Int::class.java

    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c")
    override val opplysningsNavn: String = "Antall barn som gir rett til barnetillegg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysning = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "BARNMSTON" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi?.toInt(),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
    }
}
