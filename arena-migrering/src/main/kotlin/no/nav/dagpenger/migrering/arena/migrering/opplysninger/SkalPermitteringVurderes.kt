package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

// Arena: rettighetkode PERM
class SkalPermitteringVurderes : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-9444-7a73-a458-0af81c034d86")
    override val opplysningsNavn: String = "Skal permittering vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" && rettighetstype.verdi == "PERM" }

        val gyldigFra =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning != null,
            gyldigFraOgMed = opplysning?.gyldigFra ?: gyldigFra?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
