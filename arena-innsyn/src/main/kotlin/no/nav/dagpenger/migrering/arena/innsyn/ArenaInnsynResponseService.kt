package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.server.plugins.NotFoundException
import no.nav.dagpenger.migrering.arena.api.models.ArenaSakDTO
import no.nav.dagpenger.migrering.arena.api.models.ArenaSakDetaljerDTO

class ArenaInnsynResponseService(
    private val sakRepository: ArenaSakRepositoryInterface,
    private val vedtakRepository: ArenaVedtakRepositoryInterface,
    private val vedtakfaktaRepository: ArenaVedtakFaktaRepositoryInterface,
    private val vilkårsvurderingRepository: ArenaVilkårsvurderingRepositoryInterface,
    private val kvoteBrukRepository: ArenaKvoteBrukRepositoryInterface,
    private val telleverkRepository: ArenaTelleverkRepositoryInterface,
    private val sakPersonRepository: ArenaSakPersonRepositoryInterface,
    private val personRepository: ArenaPersonRepositoryInterface,
) {
    fun hentPersonId(fodselsnr: String): Int =
        personRepository.personId(fodselsnr) ?: throw NotFoundException("Fant ikke person $fodselsnr")

    fun hentArenaSakerForPerson(ident: String): List<ArenaSakDTO> = sakPersonRepository.hentSakerForPerson(ident).map { it.tilKontrakt() }

    fun hentSak(sakId: SakId): ArenaSakDetaljerDTO {
        val sak = sakRepository.hentSak(sakId) ?: throw NotFoundException("Fant ingen sak for sakId: ${sakId.id}")

        return sakMedVedtak(sak)
    }

    fun hentSak(saksnummer: Saksnummer): ArenaSakDetaljerDTO {
        val sak =
            sakRepository.hentSak(saksnummer)
                ?: throw NotFoundException("Fant ingen sak for saksnummer: ${saksnummer.formatert()}")

        return sakMedVedtak(sak)
    }

    private fun sakMedVedtak(sak: ArenaSak): ArenaSakDetaljerDTO {
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

        val personId = PersonId(id = sak.person.personId)

        return sak
            .tilArenaSakMedVedtak(vedtakMedFaktaOgVilkår)
            .tilKontrakt(
                telleverkForPerson = telleverkRepository.hentTelleverkForPerson(personId),
                kvoteHistorikk = kvoteBrukRepository.hentKvoteBrukHendelserForPerson(personId),
                sisteUtbetalingDato = null,
            )
    }
}
