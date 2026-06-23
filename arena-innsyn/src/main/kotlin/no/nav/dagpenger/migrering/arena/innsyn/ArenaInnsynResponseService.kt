package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.server.plugins.NotFoundException

class ArenaInnsynResponseService(
    private val sakRepository: ArenaSakRepositoryInterface,
    private val vedtakRepository: ArenaVedtakRepositoryInterface,
    private val vedtakfaktaRepository: ArenaVedtakFaktaRepositoryInterface,
    private val vilkårsvurderingRepository: ArenaVilkårsvurderingRepositoryInterface,
) {
    fun hentSak(sakId: SakId): ArenaSakMedVedtak {
        val sak = sakRepository.hentSak(sakId) ?: throw NotFoundException("Fant ingen sak for sakId: ${sakId.id}")

        return sakMedVedtak(sak)
    }

    fun hentSak(saksnummer: Saksnummer): ArenaSakMedVedtak {
        val sak = sakRepository.hentSak(saksnummer) ?: throw NotFoundException("Fant ingen sak for saksnummer: ${saksnummer.formatert()}")

        return sakMedVedtak(sak)
    }

    private fun sakMedVedtak(sak: ArenaSak): ArenaSakMedVedtak {
        val vedtak = vedtakRepository.hentVedtakForSak(SakId(sak.sakId.toInt()))
        val vedtakIder = vedtak.map { it.vedtakId }

        val faktaPerVedtak = vedtakfaktaRepository.hentFaktaForVedtak(vedtakIder)
        val vilkårsvurderingerPerVedtak = vilkårsvurderingRepository.hentForVedtak(vedtakIder)

        val vedtakMedFaktaOgVilkår =
            vedtak.map { v ->
                v.medDetaljer(
                    fakta = faktaPerVedtak.filter { it.vedtakId == v.vedtakId },
                    vilkårsvurderinger = vilkårsvurderingerPerVedtak.filter { it.vedtakId == v.vedtakId },
                )
            }

        return sak.tilArenaSakMedVedtak(vedtakMedFaktaOgVilkår)
    }
}
