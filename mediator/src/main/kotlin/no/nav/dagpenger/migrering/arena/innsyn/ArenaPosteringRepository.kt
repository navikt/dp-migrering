@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class ArenaPosteringRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaPosteringRepositoryInterface {
    override fun sisteUtbetalingDato(personId: PersonId): LocalDate? = selectSingle(query, mapOf("personId" to personId.id))

    override fun mapResultat(row: ResultSet): LocalDate? = row.getDate("siste_postering")?.toLocalDate()

    val query =
        // language=oracle
        """
            select 
              max(p.dato_postert) as siste_postering       
            FROM postering p  
              join vedtak v
              on p.vedtak_id = v.vedtak_id
            WHERE v.person_id = :personId 
              AND v.rettighetkode IN ('DAGO', 'PERM', 'FISK', 'LONN')
            """
}
