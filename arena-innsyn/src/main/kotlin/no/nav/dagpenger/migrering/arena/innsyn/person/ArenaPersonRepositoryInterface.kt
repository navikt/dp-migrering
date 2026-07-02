package no.nav.dagpenger.migrering.arena.innsyn.person

import no.nav.dagpenger.migrering.arena.innsyn.ArenaRepositoryInterface

interface ArenaPersonRepositoryInterface : ArenaRepositoryInterface<ArenaPerson> {
    fun personId(fodselsnummer: String): Int?

    fun finnPerson(personId: Int): ArenaPerson?
}
