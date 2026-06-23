@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaKvoteBrukRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaKvoteBrukRepositoryInterface {
    override fun hentKvoteBrukHendelserForPerson(personId: PersonId): Set<KvotebrukHendelse> =
        select(
            sql = selectKvotePåPerson,
            params = mapOf("personId" to personId.id),
        ).toSet()

    override fun mapResultat(row: ResultSet): KvotebrukHendelse =
        KvotebrukHendelse(
            id = row.getInt("kvotebruk_id"),
            kvoteTypeKode = row.getString("kvotetypekode"),
            endringsGrunnlag = row.getString("tabellnavnalias_grunnlag"),
            antallBevegelse = row.getInt("antall_bevegelse"),
            posteringTypeKode = row.getString("posteringtypekode"),
            datoHendelse = row.getDate("dato_hendelse").toLocalDate(),
            resterende = row.getInt("resterende"),
            modUser = row.getString("mod_user"),
            begrunnelse = row.getString("begrunnelse"),
        )

    internal val selectKvotePåPerson =
        // language=oracle
        """
        SELECT /*+ INDEX (KVOTEBRUK KVOTBR_PERS_FKI) */
              KVOTEBRUK_ID
            , KVOTETYPEKODE
            , TABELLNAVNALIAS_GRUNNLAG
            , OBJEKT_ID_GRUNNLAG
            , ANTALL_BEVEGELSE
            , POSTERINGTYPEKODE
            , DATO_HENDELSE
            , MOD_DATO
            ,(select nvl(sum(k2.antall_bevegelse), 0)
              from kvotebruk k2
              where k2.person_id     = KV.Person_Id
              and k2.kvotetypekode = kv.KvotetypeKode
              and k2.kvotebruk_id <= KV.Kvotebruk_id
              and k2.kvotebruk_id >= ( select max(k3.kvotebruk_id)
                                       from kvotebruk k3
                                       where k3.person_id     = KV.Person_Id
                                       and k3.kvotetypekode = kv.KvotetypeKode
                                       and k3.kvotebruk_id <= kv.Kvotebruk_id
                                       and k3.posteringtypekode in ('INIT','NULLE'))) resterende
            , MOD_USER
            , PERSON_ID
            , BEGRUNNELSE
        FROM KVOTEBRUK kv
        WHERE person_id = :personId
          AND kv.kvotetypekode IN ('AAP', 'MAAPU')
        ORDER BY kv.dato_hendelse DESC, KVOTEBRUK_ID DESC
        """.trimIndent()
}
