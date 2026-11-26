package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.objectMapper
import java.util.UUID

// Arena: registrerte barn saksopplysning/attributt BARN FNR/FDATO/FNAVN/STONA
class BarnFraArena : BarnOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

// Levere en liste med barn inkl. "all informasjon".
    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23b")
    override val opplysningsNavn: String = "Barn fra Arena"

    override fun sammenlignVerdier(
        arenaVerdi: Any?,
        dpSakVerdi: Any?,
    ): Boolean {
        val arenaBarn = arenaVerdi?.tilBarnListe()
        val dpSakBarn = dpSakVerdi?.tilBarnListe()
        return if (arenaBarn == null || dpSakBarn == null) {
            arenaBarn == dpSakBarn
        } else {
            arenaBarn.søknadbarnId == dpSakBarn.søknadbarnId &&
                arenaBarn.barn.groupingBy { it }.eachCount() == dpSakBarn.barn.groupingBy { it }.eachCount()
        }
    }

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BarnListe> =
        Opplysning(
            navn = opplysningsNavn,
            verdi =
                BarnListe(
                    barn = emptyList(),
                ),
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}

private fun Any.tilBarnListe(): BarnListe =
    when (this) {
        is BarnListe -> this
        else -> objectMapper.convertValue(this, BarnListe::class.java)
    }
