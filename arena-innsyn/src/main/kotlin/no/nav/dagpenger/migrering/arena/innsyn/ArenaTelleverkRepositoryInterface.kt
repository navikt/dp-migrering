package no.nav.dagpenger.migrering.arena.innsyn

interface ArenaTelleverkRepositoryInterface : ArenaRepositoryInterface<KvoteVerdi> {
    fun hentTelleverkForPerson(personId: PersonId): TelleverkForPerson
}
