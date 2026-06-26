package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaSakPersonRepositoryInterface : ArenaRepositoryInterface<ArenaSak> {
    fun hentSakerForPerson(fodselsnummer: String): List<ArenaSak>
}
