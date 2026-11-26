package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: rettighetkode PERM
class SkalPermitteringVurderes : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("0194881f-9444-7a73-a458-0af81c034d86")
    override val opplysningsNavn: String = "Skal permittering vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" && rettighetstype.verdi == "PERM" }

        val gyldigFra =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning != null,
            gyldigFraOgMed = opplysning?.gyldigFra ?: gyldigFra?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
