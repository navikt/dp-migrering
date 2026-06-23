@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaVilkårsvurderingRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaVilkårsvurderingRepositoryInterface {
    override fun hentForVedtak(vedtaksIder: List<Int>): List<ArenaVilkårsvurdering> =
        select(
            sql = selectVilkårsvurderingerForVedtakIder(params = vedtaksIder.joinToString(",") { "?" }),
            params = vedtaksIder.toTypedArray(),
        )

    override fun mapResultat(row: ResultSet): ArenaVilkårsvurdering =
        ArenaVilkårsvurdering(
            vedtakId = row.getInt("vedtak_id"),
            vilkårsvurderingId = row.getLong("vilkaarvurdering_id"),
            vilkårkode = row.getString("vilkaarkode"),
            begrunnelse = row.getString("begrunnelse"),
            vurdertAv = row.getString("vurdert_av"),
            vilkårnavn = row.getString("skjermbildetekst"),
            erObligatorisk = row.getString("status_oblig") == "J",
            hjelpetekstUrl = row.getString("url_hjelpereferanse"),
            lovtekstUrl = row.getString("url_lovtekst"),
            rundskrivUrl = row.getString("url_rundskrivtekst"),
            statuskode = row.getString("vilkaarstatuskode"),
            statusnavn = row.getString("vilkaarstatusnavn"),
        )

    private fun selectVilkårsvurderingerForVedtakIder(params: String) =
        // language=oracle
        """
        SELECT vv.vilkaarvurdering_id, vv.vedtak_id, vv.vilkaarkode, vv.begrunnelse, vv.vurdert_av,
               vt.skjermbildetekst, vt.status_oblig, vt.url_hjelpereferanse, vt.url_lovtekst, vt.url_rundskrivtekst,
               vs.vilkaarstatuskode, vs.vilkaarstatusnavn
          FROM vilkaarvurdering vv
          LEFT JOIN vilkaartype vt ON vt.vilkaarkode = vv.vilkaarkode
          LEFT JOIN vilkaarstatus vs ON vs.vilkaarstatuskode = vv.vilkaarstatuskode
         WHERE vv.vedtak_id IN ($params)
        """.trimIndent()
}
