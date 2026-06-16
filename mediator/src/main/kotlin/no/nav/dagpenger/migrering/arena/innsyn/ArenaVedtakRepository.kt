package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.db.getIntOrNull
import java.sql.ResultSet
import javax.sql.DataSource

class ArenaVedtakRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaVedtakRepositoryInterface {
    override fun hentVedtakForSak(sakId: SakId): List<ArenaVedtak> =
        select(
            sql = selectVedtakForSak,
            params = mapOf("sakId" to sakId.id),
        )

    override fun mapResultat(row: ResultSet): ArenaVedtak =
        ArenaVedtak(
            vedtakId = row.getInt("vedtak_id"),
            lopenrvedtak = row.getInt("lopenrvedtak"),
            statusKode = row.getString("vedtakstatuskode"),
            statusNavn = row.getString("vedtakstatusnavn"),
            vedtaktypeKode = row.getString("vedtaktypekode"),
            vedtaktypeNavn = row.getString("vedtaktypenavn"),
            aktivitetsfaseKode = row.getString("aktfasekode"),
            aktivitetsfaseNavn = row.getString("aktfasenavn"),
            fraOgMed = row.getDate("fra_dato").toLocalDate(),
            tilDato = row.getDate("til_dato").toLocalDate(),
            rettighetkode = row.getString("rettighetkode"),
            rettighetnavn = row.getString("rettighetnavn"),
            utfallkode = row.getString("utfallkode"),
            begrunnelse = row.getString("begrunnelse"),
            saksbehandler = row.getString("brukerid_ansvarlig"),
            beslutter = row.getString("brukerid_beslutter"),
            relatertVedtak = row.getIntOrNull("vedtak_id_relatert"),
        )

    private val selectVedtakForSak =
        // language=oracle
        """
        SELECT v.vedtak_id, v.lopenrvedtak, v.vedtakstatuskode, vs.vedtakstatusnavn, v.vedtaktypekode, vt.vedtaktypenavn,
               v.fra_dato, v.til_dato, v.rettighetkode, rt.rettighetnavn, v.utfallkode, v.begrunnelse,
               v.brukerid_ansvarlig, v.brukerid_beslutter, v.vedtak_id_relatert,
               a.aktfasekode, a.aktfasenavn
          FROM vedtak v
          LEFT JOIN vedtaktype vt ON vt.vedtaktypekode = v.vedtaktypekode
          LEFT JOIN vedtakstatus vs ON v.vedtakstatuskode = vs.vedtakstatuskode
          LEFT JOIN aktivitetfase a ON a.aktfasekode = v.aktfasekode
          LEFT JOIN rettighettype rt ON rt.rettighetkode = v.rettighetkode
         WHERE sak_id = :sakId
           AND (fra_dato <= til_dato OR til_dato IS NULL)
        """.trimIndent()
}
