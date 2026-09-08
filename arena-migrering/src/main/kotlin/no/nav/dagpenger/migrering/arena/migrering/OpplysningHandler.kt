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

    fun select(
        sql: String,
        params: Map<String, Any>,
    ): List<ResultSet> =
        session { session ->
            session.run(
                queryOf(
                    sql,
                    params,
                ).map { row -> row.underlying }.asList,
            )
        }

    fun håndter(vedtakId: Int): Opplysning<T>
}
