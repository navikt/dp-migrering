package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaVilkarsvurderingResponse

data class ArenaVilkårsvurdering(
    val vilkårsvurderingId: Long,
    val vedtakId: Int,
    val vilkårkode: String,
    val begrunnelse: String?,
    val vurdertAv: String?,
    val vilkårnavn: String,
    val erObligatorisk: Boolean,
    val hjelpetekstUrl: String?,
    val lovtekstUrl: String?,
    val rundskrivUrl: String?,
    val statuskode: String,
    val statusnavn: String,
) {
    fun tilKontrakt(): ArenaVilkarsvurderingResponse =
        ArenaVilkarsvurderingResponse(
            vilkårsvurderingId = vilkårsvurderingId,
            vilkårkode = vilkårkode,
            begrunnelse = begrunnelse,
            vurdertAv = vurdertAv,
            vilkårnavn = vilkårnavn,
            erObligatorisk = erObligatorisk,
            hjelpetekstUrl = hjelpetekstUrl,
            lovtekstUrl = lovtekstUrl,
            rundskrivUrl = rundskrivUrl,
            statuskode = statuskode,
            statusnavn = statusnavn,
        )
}
