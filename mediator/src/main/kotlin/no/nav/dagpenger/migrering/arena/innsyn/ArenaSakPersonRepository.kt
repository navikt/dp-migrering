@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaSakPersonRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaSakPersonRepositoryInterface {
    override fun hentSakerForPerson(fodselsnummer: String): List<ArenaSak> =
        select(
            selectSakPåFødselsnummer,
            mapOf("fodselsnr" to fodselsnummer),
        )

    override fun mapResultat(row: ResultSet): ArenaSak = ArenaSak.fraResultSet(row)

    internal val selectSakPåFødselsnummer =
        // language=oracle
        """
        SELECT sak.sak_id,
               sak.aar,
               sak.sakstatuskode,
               sakstatus.sakstatusnavn,
               sak.lopenrsak,
               pers.person_id,
               pers.fornavn,
               pers.etternavn,
               pers.fodselsnr,
               sak.reg_dato,
               sak.dato_avsluttet
        FROM person pers
                 JOIN sak sak ON sak.objekt_id = pers.person_id
                 JOIN sakstatus ON sak.sakstatuskode = sakstatus.sakstatuskode
        WHERE pers.fodselsnr = :fodselsnr
          AND sak.tabellnavnalias = 'PERS';
        """.trimIndent()
}
