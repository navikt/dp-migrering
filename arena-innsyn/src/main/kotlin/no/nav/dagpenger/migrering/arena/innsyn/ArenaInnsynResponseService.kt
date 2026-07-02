package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.server.plugins.NotFoundException
import no.nav.dagpenger.migrering.arena.api.models.ArenaSakDetaljerResponse
import no.nav.dagpenger.migrering.arena.api.models.ArenaSakResponse
import no.nav.dagpenger.migrering.arena.innsyn.person.ArenaPerson
import no.nav.dagpenger.migrering.arena.innsyn.person.ArenaPersonRepositoryInterface

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

    fun hentPerson(personId: Int): ArenaPerson =
        personRepository.finnPerson(personId) ?: throw NotFoundException("Fant ikke person $personId")

    fun hentArenaSakerForPerson(personId: Int): List<ArenaSakResponse> {
        personRepository.finnPerson(personId) ?: throw NotFoundException("Fant ikke person $personId")
        return sakPersonRepository.hentSakerForPerson(personId).map { it.tilKontrakt() }
    }

    fun hentSak(sakId: SakId): ArenaSakDetaljerResponse {
        val sak = sakRepository.hentSak(sakId) ?: throw NotFoundException("Fant ingen sak for sakId: ${sakId.id}")

        return sakMedVedtak(sak)
    }

    fun hentSak(saksnummer: Saksnummer): ArenaSakDetaljerResponse {
        val sak =
            sakRepository.hentSak(saksnummer)
                ?: throw NotFoundException("Fant ingen sak for saksnummer: ${saksnummer.formatert()}")

        return sakMedVedtak(sak)
    }

    private fun sakMedVedtak(sak: ArenaSak): ArenaSakDetaljerResponse {
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
