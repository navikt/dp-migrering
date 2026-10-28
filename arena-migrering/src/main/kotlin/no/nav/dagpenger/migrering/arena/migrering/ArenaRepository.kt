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

    fun hentPersonInfo(sakId: Int): List<ArenaSakMigrering.ArenaPersonMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'RETTIGHETSGRUPPEKODE'   AS kolonne
                     , p.rettighetsgruppekode   AS kolonneverdi
                     , p.mod_dato
                FROM   person p
                     , sak s
                WHERE  s.sak_id = :sakId
                AND    s.tabellnavnalias = 'PERS'
                AND    s.objekt_id = p.person_id 
                UNION ALL
                SELECT 'FORMIDLINGSGRUPPEKODE' AS kolonne
                     , p.formidlingsgruppekode AS kolonneverdi
                     , p.mod_dato
                FROM   person p
                     , sak s
                WHERE  s.sak_id = :sakId
                AND    s.tabellnavnalias = 'PERS'
                AND    s.objekt_id = p.person_id 
                UNION ALL
                SELECT 'FODSELSDATO'                         AS kolonne
                     , TO_CHAR(p.fodselsdato, 'YYYY-MM-DD')  AS kolonneverdi 
                     , p.mod_dato
                FROM   person p
                     , sak s
                WHERE  s.sak_id = :sakId
                AND    s.tabellnavnalias = 'PERS'
                AND    s.objekt_id = p.person_id 
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaPersonMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.string("kolonneverdi"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentRettighetkode(sakId: Int): List<ArenaSakMigrering.ArenaRetttighetstypeMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'RETTIGHETKODE' AS kolonne
                     , rettighetkode
                     , mod_dato
                FROM   vedtak vedt
                WHERE  vedt.sak_id = :sakId
                AND    vedt.rettighetkode IN ('DAGO','PERM','FISK','LONN')
                AND    vedt.utfallkode = 'JA'
                AND    vedt.fra_dato <= NVL(vedt.til_dato, vedt.fra_dato)
                AND    vedt.vedtaktypekode IN ('O','E','G','S')
                AND    vedt.vedtakstatuskode IN ('IVERK', 'AVSLU')
                ORDER BY vedt.til_dato DESC NULLS FIRST,
                         vedt.fra_dato DESC,
                         vedt.mod_dato DESC
                FETCH FIRST 1 ROW ONLY
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaRetttighetstypeMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.string("rettighetkode"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentAntallForbrukteDagerDP(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'ForbrukteDagerDP' AS kolonne
                     , 42                 AS verdi
                     , SYSDATE            AS mod_dato
                FROM   dual
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaForbrukteDagerMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.string("verdi"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentAntallForbrukteDagerFISK(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'ForbrukteDagerFISK' AS kolonne
                     , 0                    AS verdi
                     , SYSDATE              AS mod_dato
                FROM   dual
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaForbrukteDagerMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.string("verdi"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentAntallForbrukteDagerPERM(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'ForbrukteDagerPERM' AS kolonne
                     , 0                    AS verdi
                     , SYSDATE              AS mod_dato
                FROM   dual
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaForbrukteDagerMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.string("verdi"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    fun hentSakTilMigrering(sakId: Int): ArenaSakMigrering =
        ArenaSakMigrering(
            vilkaarsVurderinger = hentVilkaarvurdering(sakId),
            vedtakfakta = hentVedtakfakta(sakId),
            personopplysninger = hentPersonInfo(sakId),
            tidsbegrensetVedtak = emptyList(),
            rettighetstype = hentRettighetkode(sakId),
            forbrukteDagerDP = hentAntallForbrukteDagerDP(sakId),
            forbrukteDagerPERM = hentAntallForbrukteDagerPERM(sakId),
            forbrukteDagerFISK = hentAntallForbrukteDagerFISK(sakId),
        )

    fun hentIdentForSak(sakId: Int): String {
        select(
            // language=oracle
            """
            SELECT pers.fodselsnr
            FROM   sak
            JOIN   person pers ON pers.person_id = sak.objekt_id
            WHERE  sak.sak_id = :sakId
            """.trimIndent(),
            mapOf("sakId" to sakId),
        ) { row ->
            row.string("fodselsnr")
        }.firstOrNull()?.let { return it }
        throw IllegalStateException("Fant ikke ident for sakId: $sakId")
    }
}
