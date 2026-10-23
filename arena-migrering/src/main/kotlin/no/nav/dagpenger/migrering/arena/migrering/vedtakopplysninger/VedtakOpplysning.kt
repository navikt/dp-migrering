package no.nav.dagpenger.migrering.arena.migrering.vedtakopplysninger

data class VedtakOpplysning<T>(
    val verdi: T,
    val navn: String,
)
