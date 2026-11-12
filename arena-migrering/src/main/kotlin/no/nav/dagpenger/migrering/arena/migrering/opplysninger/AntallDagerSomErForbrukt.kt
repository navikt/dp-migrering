package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk DPTEL
class AntallDagerSomErForbrukt : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01992934-66e4-7606-bdd3-c6c9dd420ffd")
    override val opplysningsNavn: String = "Antall dager som er forbrukt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysningDPTEL = kontekst.sakTilMigrering.gjenstaendeTellerMigrering.firstOrNull { it.kode == "DPTEL" }
        val opplysningINIT = kontekst.sakTilMigrering.opprinneligDPTELMigrering.firstOrNull { it.kode == "INITDPTEL" }

        if (opplysningDPTEL == null || opplysningINIT == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(opplysningDPTEL?.verdi, opplysningINIT?.verdi),
            gyldigFraOgMed = opplysningDPTEL?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(
        verdi1: String?,
        verdi2: String?,
    ): Int? {
        val v1 = verdi1?.toIntOrNull() ?: return null
        val v2 = verdi2?.toIntOrNull() ?: return null
        return (v2 - v1) / 200
    }
}
