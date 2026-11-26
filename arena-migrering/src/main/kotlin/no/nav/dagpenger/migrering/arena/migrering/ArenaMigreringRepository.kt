@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.inntekt.v1.Inntekt
import java.time.LocalDateTime
import javax.sql.DataSource

val kunSisteVilkaar: Set<String> = setOf("VILKAR_1", "VILKAR_2", "VILKAR_3")
val kunSisteVedtakfakta: Set<String> = setOf("VEDTAK_1", "VEDTAK_2", "VEDTAK_3")

interface ArenaMigreringRepository {
    val dataSource: Lazy<DataSource>

    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    fun <T> select(
        sql: String,
        params: Map<String, Any> = emptyMap(),
        extractor: (Row) -> T,
    ): List<T> =
        session { session ->
            session.run(
                queryOf(sql, params).map(extractor).asList,
            )
        }

    fun hentVedtakfakta(
        sakId: Int,
        kunSisteVedtakfakta: Set<String>,
    ): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering>

    fun hentSisteVedtakfakta(
        sakId: Int,
        kunSisteVedtakfakta: Set<String>,
    ): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering>

    fun hentVilkaarvurdering(
        sakId: Int,
        kunSisteVilkaar: Set<String>,
    ): List<ArenaSakMigrering.ArenaVilkaarMigrering>

    fun hentSisteVilkaarvurdering(
        sakId: Int,
        kunSisteVilkaar: Set<String>,
    ): List<ArenaSakMigrering.ArenaVilkaarMigrering>

    fun hentPersonInfo(sakId: Int): List<ArenaSakMigrering.ArenaPersonMigrering>

    fun hentFodselsdatoPerson(sakId: Int): List<ArenaSakMigrering.ArenaPersonFodselsdatoMigrering>

    fun hentRettighetkode(sakId: Int): List<ArenaSakMigrering.ArenaRetttighetstypeMigrering>

    fun hentAntallForbrukteDagerDP(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentGjenstaendeTeller(sakId: Int): List<ArenaSakMigrering.ArenaGjenstaendeTellerMigrering>

    fun hentInitiellTeller(sakId: Int): List<ArenaSakMigrering.ArenaInitiellTellerMigrering>

    fun hentAntallForbrukteDagerFISK(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentAntallForbrukteDagerPERM(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentSakTilMigrering(
        sakId: Int,
        migreringTidspunkt: LocalDateTime,
    ): ArenaSakMigrering =
        ArenaSakMigrering(
            vilkaarsVurderinger = hentVilkaarvurdering(sakId, kunSisteVilkaar),
            vedtakfakta = hentVedtakfakta(sakId, kunSisteVedtakfakta),
            personopplysninger = hentPersonInfo(sakId),
            personFodselsdatoMigrering = hentFodselsdatoPerson(sakId),
            tidsbegrensetVedtak = emptyList(),
            rettighetstype = hentRettighetkode(sakId),
            forbrukteDagerDP = hentAntallForbrukteDagerDP(sakId),
            forbrukteDagerPERM = hentAntallForbrukteDagerPERM(sakId),
            forbrukteDagerFISK = hentAntallForbrukteDagerFISK(sakId),
            inntekt = hentInntekt(sakId),
            erDeks = hentDeks(sakId),
            gjenstaendeTeller = hentGjenstaendeTeller(sakId),
            initiellTeller = hentInitiellTeller(sakId),
            aktfase = hentAktfase(sakId),
            sisteVedtakfakta = hentSisteVedtakfakta(sakId, kunSisteVedtakfakta),
            sisteVilkaarVurdering = hentSisteVilkaarvurdering(sakId, kunSisteVilkaar),
            grunnbelop = hentGrunnbelop(migreringTidspunkt),
        )

    fun hentIdentForSak(sakId: Int): String

    fun hentInntekt(sakId: Int): Inntekt?

    fun hentDeks(sakId: Int): List<ArenaSakMigrering.ArenaRetttighetstypeMigrering>

    fun hentAktfase(sakId: Int): List<ArenaSakMigrering.ArenaAktfaseMigrering>

    fun hentGrunnbelop(migreringTidspunkt: LocalDateTime): List<ArenaSakMigrering.ArenaSatsMigrering>
}
