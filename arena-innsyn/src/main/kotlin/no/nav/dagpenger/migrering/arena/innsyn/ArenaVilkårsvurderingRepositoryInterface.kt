package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaVilkårsvurderingRepositoryInterface : ArenaRepositoryInterface<ArenaVilkårsvurdering> {
    fun hentForVedtak(vedtaksIder: List<Int>): List<ArenaVilkårsvurdering>
}
