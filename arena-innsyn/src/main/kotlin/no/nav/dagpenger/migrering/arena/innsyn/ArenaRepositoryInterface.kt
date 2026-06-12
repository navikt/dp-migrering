package no.nav.dagpenger.migrering.arena.innsyn

import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import java.sql.ResultSet
import javax.sql.DataSource

interface ArenaRepositoryInterface<T> {
    val dataSource: Lazy<DataSource>

    fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

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
        session {
            it.run(
                queryOf(
                    sql,
                    params,
                ).map {
                    mapResultat(it.underlying)
                }.asList,
            )
        }
}
