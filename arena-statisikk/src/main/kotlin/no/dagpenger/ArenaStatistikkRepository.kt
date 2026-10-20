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

    fun totalMennesker(): Long =
        select(
            // language=oracle
            """
            SELECT COUNT(*) antall
            FROM   meldegruppe
            WHERE  status_aktiv = 'J'
            AND    meldegruppekode = 'DAGP'
            AND    ( (SYSDATE BETWEEN dato_fra AND NVL(dato_til, SYSDATE)) OR (dato_fra > SYSDATE) )
            """.trimIndent(),
        ) { row ->
            row.long("antall")
        }.firstOrNull() ?: 0L

    fun totalMenneskerMedRettighet(): Long =
        select(
            // language=oracle
            """
            SELECT COUNT(*) antall
            FROM   person
            WHERE  rettighetsgruppekode = 'DAGP'
            """.trimIndent(),
        ) { row ->
            row.long("antall")
        }.firstOrNull() ?: 0L

    fun tellMenneskerPerRettighetstatus(): Map<Boolean, Long> =
        mapOf(true to totalMenneskerMedRettighet(), false to (totalMennesker() - totalMenneskerMedRettighet()))
}
