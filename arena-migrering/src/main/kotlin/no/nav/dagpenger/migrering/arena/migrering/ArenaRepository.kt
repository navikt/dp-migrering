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
                SELECT vedtak_id,
                       vedtak_id_relatert,
                       LEVEL AS nivaa
                FROM   VEDTAK
                START WITH vedtak_id = :vedtakId
                CONNECT BY NOCYCLE PRIOR vedtak_id_relatert = vedtak_id
            ),
            alle_vilkaarvurderinger AS (
                SELECT vv.*,
                       av.nivaa,
                       ROW_NUMBER() OVER (
                           PARTITION BY vv.vilkaarkode
                           ORDER BY av.nivaa
                       ) AS rad_nummer
                FROM   VILKAARVURDERING vv
                JOIN   alle_vedtak av
                       ON av.vedtak_id = vv.vedtak_id
                WHERE  vv.vilkaarstatuskode != 'V'
            )
            SELECT vilkaarkode,
                   vilkaarstatuskode,
                   mod_dato,
                   nivaa
            FROM   alle_vilkaarvurderinger
            WHERE  rad_nummer = 1
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

    fun hentSakVilkaarvurdering(
        sakId: Int,
    ): List<ArenaSakMigrering.ArenaVilkaarMigrering> {
        val select = select(
            // language=oracle
            """
            SELECT vilkaarkode
                 , vilkaarstatuskode
                 , mod_dato
            FROM   vilkaarvurdering
            WHERE  vilkaarvurdering_id IN (
                     SELECT FIRST_VALUE( vilk.vilkaarvurdering_id ) OVER ( PARTITION BY vilk.vilkaarkode ORDER BY vedt.fra_dato DESC ) vilkaarvurdering_id
                     FROM   vedtak vedt
                     JOIN   vilkaarvurdering vilk ON vilk.vedtak_id = vedt.vedtak_id
                     WHERE  vedt.sak_id = :sakId
                     AND    vedt.rettighetkode IN ('DAGO','PERM','LONN','FISK')
                     AND    vedt.utfallkode = 'JA'
                     AND    vedt.fra_dato <= NVL(vedt.til_dato, vedt.fra_dato)
                     AND    vedt.vedtaktypekode IN ('O','E','G','S')
                     AND    vedt.vedtakstatuskode IN ('IVERK', 'AVSLU')
                     AND    vilk.vilkaarstatuskode IN ('J', 'N')
                   )
            """.trimIndent(),
            mapOf("sakId" to sakId),
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


