@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.migrering.objectMapper
import javax.sql.DataSource

class ArenaMigreringOracleRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaMigreringRepository {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun hentVedtakfakta(sakId: Int): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering> =
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

    override fun hentVilkaarvurdering(sakId: Int): List<ArenaSakMigrering.ArenaVilkaarMigrering> {
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

    override fun hentPersonInfo(sakId: Int): List<ArenaSakMigrering.ArenaPersonMigrering> {
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

    override fun hentFodselsdatoPerson(sakId: Int): List<ArenaSakMigrering.ArenaPersonFodselsdatoMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'FODSELSDATO'   AS kolonne
                     , p.fodselsdato   AS kolonneverdi 
                     , p.dato_fra      AS mod_dato
                FROM   person p
                     , sak s
                WHERE  s.sak_id = :sakId
                AND    s.tabellnavnalias = 'PERS'
                AND    s.objekt_id = p.person_id 
                """.trimIndent(),
                mapOf("sakId" to sakId),
            ) { row ->
                ArenaSakMigrering.ArenaPersonFodselsdatoMigrering(
                    kode = row.string("kolonne"),
                    verdi = row.localDate("kolonneverdi"),
                    gyldigFra = row.localDate("mod_dato"),
                )
            }
        return select
    }

    override fun hentRettighetkode(sakId: Int): List<ArenaSakMigrering.ArenaRetttighetstypeMigrering> {
        val select =
            select(
                // language=oracle
                """
                SELECT 'RETTIGHETKODE' AS kolonne
                     , rettighetkode
                     , mod_dato
                FROM   vedtak vedt
                WHERE  vedt.sak_id = :sakId
                AND    vedt.rettighetkode IN ('DAGO','PERM','FISK','LONN','DEKS')
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

    override fun hentAntallForbrukteDagerDP(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
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

    override fun hentAntallForbrukteDagerFISK(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
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

    override fun hentAntallForbrukteDagerPERM(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering> {
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

    override fun hentIdentForSak(sakId: Int): String {
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

    override fun hentInntekt(sakId: Int): Inntekt? {
        // Kolon i datoformatet må siteres for å unngå navngitte Kotliquery-parametere.
        // language=oracle
        return select(
            """
            SELECT JSON_OBJECT( 'inntektsId'      VALUE ( SELECT vedtakverdi
                                                            FROM   vedtakfakta
                                                            WHERE  vedtak_id = vedt.vedtak_id
                                                            AND    vedtakfaktakode = 'DDIDINNTGR' ) 
                                , 'inntektsListe'   VALUE (SELECT JSON_ARRAYAGG( 
                                                                    JSON_OBJECT( 'årMåned' VALUE aar_mnd
                                                                               , 'klassifiserteInntekter' VALUE JSON_ARRAY(
                                                                                   JSON_OBJECT( 'beløp'   VALUE TO_NUMBER(belop, '999999999.99')
                                                                                              , 'inntektKlasse' VALUE 'DAGPENGER' ))
                                                                               , 'harAvvik' VALUE NULL ) )
                                                           FROM   ( SELECT intp.vedtakverdi aar_mnd, belo.vedtakverdi belop
                                                                    FROM   vedtakfakta intp
                                                                    JOIN   vedtakfakta belo ON belo.vedtak_id = intp.vedtak_id 
                                                                                           AND belo.vedtakfaktakode = 'INTTREDS'
                                                                    WHERE  intp.vedtak_id = vedt.vedtak_id
                                                                    AND    intp.vedtakfaktakode = 'INTP3SLUTT'
                                                                    UNION ALL
                                                                    SELECT intp.vedtakverdi aar_mnd, belo.vedtakverdi belop
                                                                    FROM   vedtakfakta intp
                                                                    JOIN   vedtakfakta belo ON belo.vedtak_id = intp.vedtak_id 
                                                                                           AND belo.vedtakfaktakode = 'INTNESTS'
                                                                    WHERE  intp.vedtak_id = vedt.vedtak_id
                                                                    AND    intp.vedtakfaktakode = 'INTP2SLUTT'
                                                                    UNION ALL
                                                                    SELECT intp.vedtakverdi aar_mnd, belo.vedtakverdi belop
                                                                    FROM   vedtakfakta intp
                                                                    JOIN   vedtakfakta belo ON belo.vedtak_id = intp.vedtak_id 
                                                                                           AND belo.vedtakfaktakode = 'INTSISTE'
                                                                    WHERE  intp.vedtak_id = vedt.vedtak_id
                                                                    AND    intp.vedtakfaktakode = 'INTP1SLUTT' ) ) 
                                , 'manueltRedigert'               VALUE CASE ( SELECT vedtakverdi
                                                                                 FROM   vedtakfakta
                                                                                 WHERE  vedtak_id = vedt.vedtak_id
                                                                                 AND    vedtakfaktakode = 'MANREGINNT' )
                                                                            WHEN 'J' THEN 'true' ELSE 'false'
                                                                          END FORMAT JSON
                               , 'begrunnelseManueltRedigert'     VALUE ( SELECT begr.vedtakverdi
                                                                          FROM   vedtakfakta begr
                                                                          JOIN   vedtakfakta mreg ON mreg.vedtak_id = begr.vedtak_id AND mreg.vedtakfaktakode = 'MANREGINNT'
                                                                          WHERE  begr.vedtak_id = vedt.vedtak_id
                                                                          AND    begr.vedtakfaktakode = 'BERBESKR'
                                                                          AND    mreg.vedtakverdi = 'J' )
                               , 'sisteAvsluttendeKalenderMåned'  VALUE ( SELECT vedtakverdi
                                                                          FROM   vedtakfakta
                                                                          WHERE  vedtak_id = vedt.vedtak_id
                                                                          AND    vedtakfaktakode = 'INTP1SLUTT' )
                               , 'hentetTidspunkt'                VALUE ( SELECT reg_dato
                                                                          FROM   vedtakfakta
                                                                          WHERE  vedtak_id = vedt.vedtak_id
                                                                          AND    vedtakfaktakode = 'DDIDINNTGR' ) )  as inntekt
            FROM  vedtak vedt
            WHERE vedt.sak_id = :sakId
            AND   vedt.rettighetkode in ('DAGO','PERM','FISK','LONN')
            AND   vedt.vedtaktypekode IN ('O','E','G')
            AND   vedt.vedtakstatuskode = 'IVERK'
            AND   vedt.utfallkode = 'JA'
            AND   vedt.fra_dato <= nvl(vedt.til_dato, vedt.fra_dato)
            AND   NVL( vedt.til_dato, SYSDATE ) >= SYSDATE
            """.trimIndent(),
            mapOf("sakId" to sakId),
        ) { row ->
            row.string("inntekt")
        }.firstOrNull()?.let {
            logger.info { "Inntekt json for sakId $sakId: $it" }
            val inntekt = objectMapper.readValue(it, Inntekt::class.java)
            logger.info { "Inntekt for sakId $sakId: $inntekt" }
            return inntekt
        }
    }
}
