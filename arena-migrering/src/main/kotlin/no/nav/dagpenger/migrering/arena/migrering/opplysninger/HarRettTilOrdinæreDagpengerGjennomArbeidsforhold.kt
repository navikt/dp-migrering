package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: rettighetkode DAGO
class HarRettTilOrdinæreDagpengerGjennomArbeidsforhold : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9444-7a73-a458-0af81c034d85")
    override val opplysningsNavn: String = "Har rett til ordinære dagpenger gjennom arbeidsforhold"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" && rettighetstype.verdi == "DAGO" }

        val gyldigFra =
            kontekst.sakTilMigrering.rettighetstype
                .firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning != null,
            gyldigFraOgMed = opplysning?.gyldigFra ?: gyldigFra?.gyldigFra,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
    }
}
