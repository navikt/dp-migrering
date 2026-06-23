package no.nav.dagpenger.migrering.arena.innsyn

import java.time.LocalDate

data class ArenaVedtak(
    val vedtakId: Int,
    val lopenrvedtak: Int,
    val statusKode: String,
    val statusNavn: String,
    val vedtaktypeKode: String,
    val vedtaktypeNavn: String,
    val aktivitetsfaseKode: String,
    val aktivitetsfaseNavn: String,
    val fraOgMed: LocalDate?,
    val tilDato: LocalDate?,
    val rettighetkode: String,
    val rettighetnavn: String,
    val utfallkode: String?,
    val begrunnelse: String?,
    val saksbehandler: String?,
    val beslutter: String?,
    val relatertVedtak: Int?,
) {
    fun medDetaljer(
        fakta: List<ArenaVedtakfakta>,
        vilkårsvurderinger: List<ArenaVilkårsvurdering> = emptyList(),
    ) = ArenaVedtakMedDetaljer(
        vedtakId = vedtakId,
        lopenrvedtak = lopenrvedtak,
        statusKode = statusKode,
        statusNavn = statusNavn,
        vedtaktypeKode = vedtaktypeKode,
        vedtaktypeNavn = vedtaktypeNavn,
        aktivitetsfaseKode = aktivitetsfaseKode,
        aktivitetsfaseNavn = aktivitetsfaseNavn,
        fraOgMed = fraOgMed,
        tilDato = tilDato,
        rettighetkode = rettighetkode,
        rettighetnavn = rettighetnavn,
        utfallkode = utfallkode,
        begrunnelse = begrunnelse,
        saksbehandler = saksbehandler,
        beslutter = beslutter,
        relatertVedtak = relatertVedtak,
        fakta = fakta,
        vilkårsvurderinger = vilkårsvurderinger,
    )
}
