package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource

interface OpplysningHandler<T> {
    val opplysnigsId: UUID
    val dataSource: Lazy<DataSource>

    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    fun mapResultat(row: ResultSet): Opplysning<T>

    fun select(
        sql: String,
        params: Map<String, Any>,
    ): Opplysning<T> =
        session { session ->
            session.run(
                queryOf(
                    sql,
                    params,
                ).map { row ->
                    mapResultat(row.underlying)
                }.asSingle,
            )!!
        }

    fun håndter(): Opplysning<T>
}
