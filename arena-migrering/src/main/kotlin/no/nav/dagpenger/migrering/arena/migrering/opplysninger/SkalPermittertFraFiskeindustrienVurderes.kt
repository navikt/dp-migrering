package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

class SkalPermittertFraFiskeindustrienVurderes : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val opplysningsType: Class<Boolean> = Boolean::class.java
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-9444-7a73-a458-0af81c034d88")
    override val opplysningsNavn: String = "Skal permittert fra fiskeindustrien vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" && rettighetstype.verdi == "FISK" }

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
