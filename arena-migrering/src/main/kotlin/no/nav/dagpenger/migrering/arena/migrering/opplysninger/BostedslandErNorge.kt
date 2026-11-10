package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// TODO Endre fra OppholdINorge? Hva med grensearbeidere, asylsøkere mm
// Arena: spør noen voksne vilkår INORGE evt freg i Norge personforhold
class BostedslandErNorge : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    // Levere en liste med barn inkl. "all informasjon".
    override val opplysningsId: UUID = UUID.fromString("0196ab10-0cff-7301-99d6-65be50a50201")
    override val opplysningsNavn: String = "BostedslandErNorg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
