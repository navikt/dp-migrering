package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import javax.sql.DataSource

class ArenaRepository(
    val dataSource: Lazy<DataSource>,
) {
    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    fun <T> select(
        sql: String,
        params: Map<String, Any> = emptyMap(),
        extractor: (Row) -> T,
    ): List<T> =
        session { session ->
            session.run(
                queryOf(sql, params).map(extractor).asList,
            )
        }
}
