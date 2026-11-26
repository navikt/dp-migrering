package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk DPTEL
class AntallDagerSomGjenstarDP : HeltallsOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("01992956-e349-76b1-8f68-c9d481df3a32")
    override val opplysningsNavn: String = "Antall dager som gjenstår"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysning =
            kontekst.sakTilMigrering.gjenstaendeTeller.firstOrNull { it.kode == "GJENNTELLER" && it.kodetype == "DPTEL" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = (opplysning?.verdi?.toInt() ?: 0).div(20),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
