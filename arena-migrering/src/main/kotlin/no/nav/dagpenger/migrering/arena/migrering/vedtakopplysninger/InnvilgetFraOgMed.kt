package no.nav.dagpenger.migrering.arena.migrering.vedtakopplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate

class InnvilgetFraOgMed : VedtakOpplysningProdusent<LocalDate> {
    override suspend fun produser(kontekst: MigreringsKontekst): VedtakOpplysning<LocalDate> =
        VedtakOpplysning(
            verdi = LocalDate.now(),
            navn = "innvilgetFraOgMed",
        )
}
