package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaSakRepositoryInterface : ArenaRepositoryInterface<ArenaSak> {
    fun hentSak(sakId: SakId): ArenaSak?

    fun hentSak(saksnummer: Saksnummer): ArenaSak?
}
