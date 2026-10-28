package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import java.time.LocalDate

class Fodselsdato : OpplysningProdusent<LocalDate> {
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-940b-76ff-acf5-ba7bcb367233")
    override val opplysningsNavn: String = "Fødselsdato"

    override suspend fun produser(kontekst: no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst): Opplysning<LocalDate> {
        val opplysning = kontekst.sakTilMigrering.personopplysninger.firstOrNull { it.kode == "FODSELSDATO" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi?.let { LocalDate.parse(it) },
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
