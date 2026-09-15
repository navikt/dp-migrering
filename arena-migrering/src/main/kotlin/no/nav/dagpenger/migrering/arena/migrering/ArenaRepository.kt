@file:Suppress("SqlResolve")

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

    fun hentVedtakfakta(
        vedtakId: Int,
    ): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering> =
        select(
            // language=oracle
            """
            SELECT vedtakfaktakode
                 , vedtakverdi
                 , mod_dato 
            FROM   vedtakfakta 
            WHERE  vedtak_id = :vedtakId 
            AND    vedtakverdi IS NOT NULL
            """.trimIndent(),
            mapOf("vedtakId" to vedtakId),
        ) { row ->
            ArenaSakMigrering.ArenaVedtakfaktaMigrering(
                kode = row.string("vedtakfaktakode"),
                verdi = row.string("vedtakverdi"),
                gyldigFra = row.localDate("mod_dato"),
            )
        }

    fun hentVilkaarvurdering(
        vedtakId: Int,
    ): List<ArenaSakMigrering.ArenaVilkaarMigrering> {
        val select = select(
            // language=oracle
            """
            WITH alle_vedtak AS (
                SELECT VEDTAK_ID,
                       VEDTAK_ID_RELATERT,
                       LEVEL AS nivaa
                FROM   VEDTAK
                START WITH VEDTAK_ID = :vedtakId
                CONNECT BY NOCYCLE PRIOR VEDTAK_ID_RELATERT = VEDTAK_ID
            ),
            alle_vilkaarvurderinger AS (
                SELECT vv.*,
                       av.nivaa,
                       ROW_NUMBER() OVER (
                           PARTITION BY vv.VILKAARKODE
                           ORDER BY av.nivaa
                       ) AS rad_nummer
                FROM   VILKAARVURDERING vv
                JOIN   alle_vedtak av
                       ON av.VEDTAK_ID = vv.VEDTAK_ID
                WHERE  vv.VILKAARSTATUSKODE != 'V'
            )
            SELECT VILKAARKODE,
                   VILKAARSTATUSKODE,
                   MOD_DATO,
                   nivaa
            FROM   alle_vilkaarvurderinger
            WHERE  rad_nummer = 1
            ORDER  BY VILKAARKODE
                """.trimIndent(),
            mapOf("vedtakId" to vedtakId),
        ) { row ->
            ArenaSakMigrering.ArenaVilkaarMigrering(
                kode = row.string("vilkaarstatuskode"),
                verdi = row.string("vilkaarkode"),
                gyldigFra = row.localDate("mod_dato"),
            )
        }
        return select
    }
}


