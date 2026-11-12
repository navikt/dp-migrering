@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.inntekt.v1.Inntekt
import javax.sql.DataSource

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

    fun hentVedtakfakta(sakId: Int): List<ArenaSakMigrering.ArenaVedtakfaktaMigrering>

    fun hentVilkaarvurdering(sakId: Int): List<ArenaSakMigrering.ArenaVilkaarMigrering>

    fun hentPersonInfo(sakId: Int): List<ArenaSakMigrering.ArenaPersonMigrering>

    fun hentFodselsdatoPerson(sakId: Int): List<ArenaSakMigrering.ArenaPersonFodselsdatoMigrering>

    fun hentRettighetkode(sakId: Int): List<ArenaSakMigrering.ArenaRetttighetstypeMigrering>

    fun hentAntallForbrukteDagerDP(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentAntallForbrukteDagerFISK(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentAntallForbrukteDagerPERM(sakId: Int): List<ArenaSakMigrering.ArenaForbrukteDagerMigrering>

    fun hentSakTilMigrering(sakId: Int): ArenaSakMigrering =
        ArenaSakMigrering(
            vilkaarsVurderinger = hentVilkaarvurdering(sakId),
            vedtakfakta = hentVedtakfakta(sakId),
            personopplysninger = hentPersonInfo(sakId),
            personFodselsdatoMigrering = hentFodselsdatoPerson(sakId),
            tidsbegrensetVedtak = emptyList(),
            rettighetstype = hentRettighetkode(sakId),
            forbrukteDagerDP = hentAntallForbrukteDagerDP(sakId),
            forbrukteDagerPERM = hentAntallForbrukteDagerPERM(sakId),
            forbrukteDagerFISK = hentAntallForbrukteDagerFISK(sakId),
            inntekt = hentInntekt(sakId),
            gjenstaendeTellerMigrering = hentGjenstaendeTellerverdi(sakId),
            opprinneligDPTELMigrering = hentOpprinneligDPTEL(sakId),
        )

    fun hentIdentForSak(sakId: Int): String

    fun hentInntekt(sakId: Int): Inntekt?

    fun hentGjenstaendeTellerverdi(sakId: Int): List<ArenaSakMigrering.ArenaGjenstaendeTellerMigrering>

    fun hentOpprinneligDPTEL(sakId: Int): List<ArenaSakMigrering.ArenaOpprinneligDPTELMigrering>
}
