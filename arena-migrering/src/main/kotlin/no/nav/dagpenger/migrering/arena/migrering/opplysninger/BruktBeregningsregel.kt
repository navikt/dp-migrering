package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: ? vedtakfakta BERREGEL
class BruktBeregningsregel : TekstOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cba")
    override val opplysningsNavn: String = "Brukt beregningsregel"

//  Vedtaksfakta. Må nok konverteres til DP-sak sine verdier for beregningsregler. DPGRLGREGL (beregningsregelverk pr node)

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> {
        val fakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { fakta -> fakta.kode == "BERREGEL" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta?.verdi,
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
