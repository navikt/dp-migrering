package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaKvoteBrukRepositoryInterface : ArenaRepositoryInterface<KvotebrukHendelse> {
    fun hentKvoteBrukHendelserForPerson(personId: PersonId): Set<KvotebrukHendelse>
}
