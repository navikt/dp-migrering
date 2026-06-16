package no.nav.dagpenger.migrering.arena.innsyn

import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import java.sql.ResultSet
import javax.sql.DataSource

interface ArenaRepositoryInterface<T> {
    val dataSource: Lazy<DataSource>

    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    fun mapResultat(row: ResultSet): T

    fun selectSingle(
        sql: String,
        params: Map<String, Any>,
    ): T? =
        select(
            sql,
            params,
        ).firstOrNull()

    fun select(
        sql: String,
        params: Map<String, Any>,
    ): List<T> =
        session { session ->
            session.run(
                queryOf(
                    sql,
                    params,
                ).map { row ->
                    mapResultat(row.underlying)
                }.asList,
            )
        }

    fun select(
        sql: String,
        vararg params: Any?,
    ): List<T> =
        session { session ->
            session.run(
                queryOf(
                    sql,
                    params,
                ).map { row ->
                    mapResultat(row.underlying)
                }.asList,
            )
        }
}
