package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk DPTEL
class AntallDagerSomErForbrukt : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("01992934-66e4-7606-bdd3-c6c9dd420ffd")
    override val opplysningsNavn: String = "Antall dager som er forbrukt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysningGjen =
            kontekst.sakTilMigrering.gjenstaendeTeller.firstOrNull { it.kode == "GJENNTELLER" && it.kodetype == "DPTEL" }
        val opplysningInit =
            kontekst.sakTilMigrering.initiellTeller.firstOrNull { it.kode == "INITTELLER" && it.kodetype == "DPTEL" }

        if (opplysningGjen == null || opplysningInit == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = ((opplysningInit?.verdi?.toInt() ?: 0).minus(opplysningGjen?.verdi?.toInt() ?: 0)).div(20),
            gyldigFraOgMed = opplysningInit?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
