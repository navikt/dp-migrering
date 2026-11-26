package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Vedtakfakta ARBTIDREG og verdi "Siste arbeidsforhold" eller bedre Vedtakfakta ARBTREGKOD og verdi ARBFORSIST
class BeregningsregelArbeidstidSiste6Maneder : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a764")
    override val opplysningsNavn: String = "Beregningsregel: Arbeidstid siste 6 måneder"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val fakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { fakta -> fakta.kode == "ARBTIDREG" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(fakta?.verdi),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "ARBFORSIST" -> {
                true
            }
            else -> {
                false
            }
        }
}
