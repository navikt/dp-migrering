package no.nav.dagpenger.migrering.arena.innsyn

import com.natpryce.konfig.ConfigurationMap
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.server.application.Application
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.auth.AuthFactory.azure_app
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.AZUREAD_ISSUER_ID
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.CLIENT_ID
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.mockOAuth2Server
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.testAzureAdToken
import no.nav.dagpenger.migrering.konfigurasjon.Configuration

class ArenaInnsynSystem(
    val oppsett: ScenarioOptions,
) {
    companion object {
        fun nyttScenario(block: ScenarioOptions.() -> Unit = {}) = ScenarioOptions().apply(block)
    }

    private val authFactory =
        AuthFactory(
            ConfigurationMap(
                mapOf(
                    Configuration.Grupper.saksbehandler.name to oppsett.saksbehandlerGruppe,
                    // Configuration.Maskintilgang.navn.name to oppsett,
                    azure_app.client_id.name to CLIENT_ID,
                    azure_app.well_known_url.name to "${
                        mockOAuth2Server.wellKnownUrl(
                            AZUREAD_ISSUER_ID,
                        )
                    }",
                ),
            ),
        )

    val api: Application.() -> Unit = { arenaInnsynApi(authFactory) }

    class ScenarioOptions(
        var personId: PersonId? = null,
        var saksnummer: Saksnummer? = null,
        var saksbehandlerGruppe: String = "dagpenger-saksbehandler",
    ) {
        inline fun test(crossinline block: ArenaInnsynSystem.() -> Unit) {
            val test = ArenaInnsynSystem(this@ScenarioOptions)
            test.block()
        }
    }

    internal suspend fun TestContext.autentisert(
        httpMethod: HttpMethod = HttpMethod.Get,
        endepunkt: String,
        body: String? = null,
        adgrupper: List<String> = listOf(oppsett.saksbehandlerGruppe),
        token: String =
            testAzureAdToken(
                ADGrupper = adgrupper,
                navIdent = "Z999999",
            ),
    ): HttpResponse =
        client.request(endepunkt) {
            this.method = httpMethod
            body?.let { this.setBody(TextContent(it, ContentType.Application.Json)) }
            this.header(HttpHeaders.Authorization, "Bearer $token")
            this.header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

    internal suspend fun TestContext.uautentisert(
        httpMethod: HttpMethod = HttpMethod.Get,
        endepunkt: String,
    ): HttpResponse =
        client.request(endepunkt) {
            this.method = httpMethod
            this.header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
}
