package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaPersonRepositoryInterface : ArenaRepositoryInterface<ArenaPerson> {
    fun personId(fodselsnummer: String): Int?
}
