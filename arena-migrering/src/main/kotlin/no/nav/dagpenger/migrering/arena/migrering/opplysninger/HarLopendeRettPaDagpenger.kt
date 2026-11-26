package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: rettighetsgruppe DAGP
class HarLopendeRettPaDagpenger : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa
    override val opplysningsId: UUID = UUID.fromString("01990a09-0eab-7957-b88f-14484a50e194")
    override val opplysningsNavn: String = "Har løpende rett på dagpenger"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val personopplysninger =
            kontekst.sakTilMigrering.personopplysninger
                .firstOrNull { personopplysninger -> personopplysninger.kode == "RETTIGHETSGRUPPEKODE" }

        if (personopplysninger == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(personopplysninger?.verdi),
            gyldigFraOgMed = personopplysninger?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "DAGP" -> {
                true
            }

            else -> {
                false
            }
        }
}
