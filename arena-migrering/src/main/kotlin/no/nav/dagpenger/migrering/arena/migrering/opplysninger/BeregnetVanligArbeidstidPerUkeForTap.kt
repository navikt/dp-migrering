package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Vedtakfakta FASTARBTID
class BeregnetVanligArbeidstidPerUkeForTap : DesimaltallsOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa
    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a767")
    override val opplysningsNavn: String = "Beregnet vanlig arbeidstid per uke før tap"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> {
        val fakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { fakta -> fakta.kode == "FASTARBTID" }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta?.verdi?.toDouble(),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
