package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet

interface ArenaSakRepositoryInterface {
    fun hentSak(sakId: SakId): ArenaSak?

    fun hentSak(saksnummer: Saksnummer): ArenaSak?

    fun map(row: ResultSet): ArenaSak
}
