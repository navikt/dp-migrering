package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaVedtakFaktaRepositoryInterface : ArenaRepositoryInterface<ArenaVedtakfakta> {
    fun hentFaktaForVedtak(vedtakIder: List<Int>): List<ArenaVedtakfakta>
}
