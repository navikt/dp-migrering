@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaSakRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaSakRepositoryInterface {
    override fun hentSak(sakId: SakId): ArenaSak? =
        selectSingle(
            selectSakMedSaksId,
            mapOf("sakId" to sakId.id),
        )

    override fun hentSak(saksnummer: Saksnummer): ArenaSak? =
        selectSingle(selectSakMedSaksnummer, mapOf("lopenummer" to saksnummer.lopenummer, "aar" to saksnummer.aar))

    override fun mapResultat(row: ResultSet): ArenaSak =
        ArenaSak(
            sakId = row.getInt("sak_id").toString(),
            opprettetAar = row.getInt("aar").toString(),
            lopenr = row.getInt("lopenrsak"),
            statuskode = row.getString("sakstatuskode"),
            statusnavn = row.getString("sakstatuskode"), // fallback to code if name not available
            registrertDato = row.getTimestamp("reg_dato").toLocalDateTime(),
            avsluttetDato = row.getTimestamp("dato_avsluttet")?.toLocalDateTime(),
            person =
                ArenaSakPerson(
                    personId = row.getInt("person_id"),
                    fodselsnummer = row.getString("fodselsnr"),
                    fornavn = row.getString("fornavn"),
                    etternavn = row.getString("etternavn"),
                ),
        )

    // language=oracle
    internal val selectSakMedSaksId =
        """
        SELECT sak.sak_id, sak.aar, sak.sakstatuskode, sakstatus.sakstatusnavn, sak.lopenrsak, person.person_id, 
                person.fornavn, person.etternavn, person.fodselsnr, sak.reg_dato, sak.dato_avsluttet
            FROM SAK
            LEFT JOIN person ON person.person_id = sak.objekt_id
            LEFT JOIN sakstatus ON sak.sakstatuskode = sakstatus.sakstatuskode
            WHERE SAK_ID = :sakId AND TABELLNAVNALIAS='PERS'
        """.trimIndent()

    internal val selectSakMedSaksnummer =
        // language=oracle
        """
        SELECT sak.sak_id, sak.aar, sak.sakstatuskode, sakstatus.sakstatusnavn, sak.lopenrsak, person.person_id, 
            person.fornavn, person.etternavn, person.fodselsnr, sak.reg_dato, sak.dato_avsluttet
        FROM SAK
        LEFT JOIN person ON person.person_id = sak.objekt_id
        LEFT JOIN sakstatus ON sak.sakstatuskode = sakstatus.sakstatuskode
        WHERE LOPENRSAK = :lopenummer AND AAR = :aar AND TABELLNAVNALIAS='PERS'
        """.trimIndent()
}
