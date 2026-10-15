package no.dagpenger

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import javax.sql.DataSource

class ArenaStatistikkRepository(
    val dataSource: Lazy<DataSource>,
) {
    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    private fun <T> select(
        sql: String,
        params: Map<String, Any> = emptyMap(),
        extractor: (Row) -> T,
    ): List<T> =
        session { session ->
            session.run(
                queryOf(sql, params).map(extractor).asList,
            )
        }

    fun tellMenneskerPerRettighetstatus(): Map<Boolean, Long> =
        select(
            """
            SELECT rettighetstatus, COUNT(*) AS antall
            FROM arena_rettighetstatus
            GROUP BY rettighetstatus
            """.trimIndent(),
        ) { row ->
            row.boolean("rettighetstatus") to row.long("antall")
        }.toMap()
}
