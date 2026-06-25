package no.nav.dagpenger.migrering.arena.innsyn

import java.time.LocalDate

interface ArenaPosteringRepositoryInterface : ArenaRepositoryInterface<LocalDate?> {
    fun sisteUtbetalingDato(personId: PersonId): LocalDate?
}
