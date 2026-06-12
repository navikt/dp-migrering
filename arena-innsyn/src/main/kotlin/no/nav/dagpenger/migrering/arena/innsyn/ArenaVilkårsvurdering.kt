package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaVilkarsvurderingDTO

data class ArenaVilkårsvurdering(
    val vilkårsvurderingId: Long,
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
    fun tilKontrakt(): ArenaVilkarsvurderingDTO =
        ArenaVilkarsvurderingDTO(
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
