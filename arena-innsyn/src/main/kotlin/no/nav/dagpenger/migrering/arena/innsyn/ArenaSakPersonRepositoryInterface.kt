package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaSakPersonRepositoryInterface : ArenaRepositoryInterface<ArenaSak> {
    fun hentSakerForPerson(personId: Int): List<ArenaSak>
}
