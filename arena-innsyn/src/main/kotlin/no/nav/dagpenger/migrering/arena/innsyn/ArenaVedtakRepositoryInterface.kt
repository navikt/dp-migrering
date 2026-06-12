package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaVedtakRepositoryInterface : ArenaRepositoryInterface<ArenaVedtak> {
    fun hentVedtakForSak(sakId: SakId): List<ArenaVedtak>
}
