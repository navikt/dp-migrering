package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkaar IFYT IFSYKEP IFUFTRY IFFODSP IFGAFISK IFAFP IKKEFERIE hvis penger igjen så ikke fulle
class OppfyllerVilkaretOmAIkkeMottaAndreFulleYtelser : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java
    override val opplysningsId: UUID = UUID.fromString("0194881f-943f-78d9-b874-00a4944c54f1")
    override val opplysningsNavn: String = "Oppfyller vilkåret om å ikke motta andre fulle ytelser"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
