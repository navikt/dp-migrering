package no.nav.dagpenger.migrering.arena.migrering

import com.natpryce.konfig.ConfigurationMap
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.serialization.jackson3.JacksonConverter
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.sse.SSE
import io.ktor.server.testing.testApplication
import no.dagpenger.stpeter.plugin.StPeterConfig
import no.dagpenger.stpeter.plugin.StPeterPlugin
import no.dagpenger.stpeter.plugin.test.StPeterWithOAuthMock
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.auth.AuthFactory.azure_app
import no.nav.dagpenger.migrering.api.authenticationConfig
import no.nav.dagpenger.migrering.api.statusPagesConfig
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.testAzureAdToken
import no.nav.dagpenger.migrering.konfigurasjon.Configuration
import no.nav.dagpenger.migrering.objectMapper

class TestContext(
    val client: HttpClient,
) {
    internal suspend fun autentisert(
        httpMethod: HttpMethod = HttpMethod.Get,
        endepunkt: String,
        body: String? = null,
        adgrupper: List<String> = listOf(TestApplication.SAKSBEHANDLER_GRUPPE),
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

    internal suspend fun uautentisert(
        httpMethod: HttpMethod = HttpMethod.Get,
        endepunkt: String,
    ): HttpResponse =
        client.request(endepunkt) {
            this.method = httpMethod
            this.header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            this.header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
}

object TestApplication {
    internal const val AZUREAD_ISSUER_ID = "azureAd"
    internal const val CLIENT_ID = "dp-arena-innsyn"
    internal const val SAKSBEHANDLER_GRUPPE = "dagpenger-saksbehandler"

    internal val mockOAuth2Server: StPeterWithOAuthMock by lazy {
        StPeterWithOAuthMock().also { server ->
            server.start()
        }
    }
    private val authFactory =
        AuthFactory(
            ConfigurationMap(
                mapOf(
                    Configuration.Grupper.saksbehandler.name to "dagpenger-saksbehandler",
                    // Configuration.Maskintilgang.navn.name to oppsett,
                    azure_app.client_id.name to CLIENT_ID,
                    azure_app.well_known_url.name to "${mockOAuth2Server.config()["AZURE_APP_WELL_KNOWN_URL"]}",
                ),
            ),
        )

    internal fun testAzureAdToken(
        ADGrupper: List<String>,
        navIdent: String,
    ): String =
        mockOAuth2Server
            .issueToken(
                issuerId = AZUREAD_ISSUER_ID,
                audience = CLIENT_ID,
                claims =
                    mapOf(
                        "NAVident" to navIdent,
                        "groups" to ADGrupper,
                    ),
            )

    internal fun withMockAuthServerAndTestApplication(
        migreringMediator: MigreringMediator,
        stPeter: StPeterPlugin = StPeterPlugin(config = StPeterConfig(mockOAuth2Server.config())),
        test: suspend TestContext.() -> Unit,
    ) {
        testApplication {
            application {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    register(ContentType.Application.Json, JacksonConverter(objectMapper))
                }
                install(StatusPages) {
                    statusPagesConfig()
                }
                install(SSE)
                authenticationConfig(authFactory)
                arenaMigreringApi(
                    migreringMediator = migreringMediator,
                    stPeter = stPeter,
                )
            }

            val testClient =
                createClient {
                    install(ContentNegotiation) {
                        register(ContentType.Application.Json, JacksonConverter(objectMapper))
                    }
                }

            test(TestContext(testClient))
        }
    }
}
