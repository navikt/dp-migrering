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
            // language=oracle
            """
            SELECT 'false' hva, COUNT(*) antall
            FROM   meldegruppe
            WHERE  status_aktiv = 'J'
            AND    meldegruppekode = 'DAGP'
            AND    ( (SYSDATE BETWEEN dato_fra AND NVL(dato_til, SYSDATE)) OR (dato_fra > SYSDATE) )
            UNION ALL
            SELECT 'true' hva, COUNT(*) antall
            FROM   person
            WHERE  rettighetsgruppekode = 'DAGP';
            """.trimIndent(),
        ) { row ->
            row.boolean("hva") to row.long("antall")
        }.toMap()
}
