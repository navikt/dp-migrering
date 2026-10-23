package no.nav.dagpenger.migrering.arena.migrering.vedtakopplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

interface VedtakOpplysningProdusent<T> {
    suspend fun produser(kontekst: MigreringsKontekst): VedtakOpplysning<T>
}
