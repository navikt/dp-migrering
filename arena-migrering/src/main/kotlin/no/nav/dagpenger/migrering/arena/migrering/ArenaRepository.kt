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

    fun hentVedtakfakta(sakId: Int): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering> =
        select(
            // language=oracle
            """
            SELECT vedtakfaktakode
                 , vedtakverdi
                 , mod_dato
            FROM   vedtakfakta
            WHERE  (vedtak_id, vedtakfaktakode) IN (
                     SELECT FIRST_VALUE( vefa.vedtak_id ) OVER ( PARTITION BY vefa.vedtakfaktakode ORDER BY vedt.fra_dato DESC ) vedtak_id
                          , vefa.vedtakfaktakode
                     FROM   vedtak vedt
                     JOIN   vedtakfakta vefa ON vefa.vedtak_id = vedt.vedtak_id
                     WHERE  vedt.sak_id = :sakId
                     AND    vedt.rettighetkode IN ('DAGO','PERM','FISK','LONN')
                     AND    vedt.utfallkode = 'JA'
                     AND    vedt.fra_dato <= NVL(vedt.til_dato, vedt.fra_dato)
                     AND    vedt.vedtaktypekode IN ('O','E','G','S')
                     AND    vedt.vedtakstatuskode IN ('IVERK', 'AVSLU')
                     AND    vefa.vedtakverdi IS NOT NULL
                   )
            AND    vedtakverdi IS NOT NULL
            """.trimIndent(),
            mapOf("sakId" to sakId),
        ) { row ->
            ArenaSakMigrering.ArenaVedtakfaktaMigrering(
                kode = row.string("vedtakfaktakode"),
                verdi = row.string("vedtakverdi"),
                gyldigFra = row.localDate("mod_dato"),
            )
        }

    fun hentVilkaarvurdering(sakId: Int): List<ArenaSakMigrering.ArenaVilkaarMigrering> {
        val select =
            select(
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
                         AND    vedt.rettighetkode IN ('DAGO','PERM','FISK','LONN')
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
                    kode = row.string("vilkaarkode"),
                    verdi = row.string("vilkaarstatuskode"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentSakTilMigrering(sakId: Int): ArenaSakMigrering =
        ArenaSakMigrering(
            vilkaarsVurderinger = hentVilkaarvurdering(sakId),
            vedtakfakta = hentVedtakfakta(sakId),
        )
}
