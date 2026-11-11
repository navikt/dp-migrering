package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: rettighetkode DEKS
class BrukerSkalHaEksportAvDagpengerTilEOSLand : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val opplysningsId: UUID = UUID.fromString("019f608e-807e-758c-80ff-58e95f429f5a")
    override val opplysningsNavn: String = "Bruker skal ha eksport av dagpenger til EØS-land"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning = kontekst.sakTilMigrering.rettighetstype.firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(opplysning?.verdi),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "DEKS" -> {
                true
            }
            else -> {
                false
            }
        }
}
