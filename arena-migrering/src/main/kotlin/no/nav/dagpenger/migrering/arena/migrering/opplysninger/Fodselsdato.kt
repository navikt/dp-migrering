package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import java.time.LocalDate
import java.util.UUID

// Arena: person fodselsdato
class Fodselsdato : OpplysningProdusent<LocalDate> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert
    override val opplysningsId: UUID = UUID.fromString("0194881f-940b-76ff-acf5-ba7bcb367233")
    override val opplysningsNavn: String = "Fødselsdato"

    override suspend fun produser(kontekst: no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst): Opplysning<LocalDate> {
        val opplysning = kontekst.sakTilMigrering.personFodselsdatoMigrering.firstOrNull { it.kode == "FODSELSDATO" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi,
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
