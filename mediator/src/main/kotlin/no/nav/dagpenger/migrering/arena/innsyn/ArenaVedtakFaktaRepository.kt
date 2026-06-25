@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaVedtakFaktaRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaVedtakFaktaRepositoryInterface {
    override fun hentFaktaForVedtak(vedtakIder: List<Int>): List<ArenaVedtakfakta> {
        if (vedtakIder.isEmpty()) return emptyList()

        return select(
            sql = selectVedtakfaktaForVedtakIder(params = vedtakIder.joinToString(",") { "?" }),
            params = vedtakIder,
        )
    }

    override fun mapResultat(row: ResultSet): ArenaVedtakfakta =
        ArenaVedtakfakta(
            vedtakId = row.getInt("vedtak_id"),
            kode = row.getString("vedtakfaktakode"),
            navn = row.getString("skjermbildetekst"),
            verdi = row.getString("vedtakverdi"),
            registrertDato = row.getDate("reg_dato").toLocalDate(),
        )

    private fun selectVedtakfaktaForVedtakIder(params: String) =
        // language=oracle
        """
        SELECT vf.vedtak_id, vf.vedtakfaktakode, vf.vedtakverdi, vf.reg_dato, vft.skjermbildetekst
          FROM vedtakfakta vf
          LEFT JOIN vedtakfaktatype vft ON vft.vedtakfaktakode = vf.vedtakfaktakode
         WHERE vf.vedtak_id IN ($params)
        """.trimIndent()
}
