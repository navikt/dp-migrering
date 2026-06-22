package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.jackson3.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.testing.testApplication
import no.nav.dagpenger.migrering.api.statusPagesConfig
import no.nav.dagpenger.migrering.objectMapper
import no.nav.security.mock.oauth2.MockOAuth2Server

class TestContext(
    val client: HttpClient,
)

object TestApplication {
    internal const val AZUREAD_ISSUER_ID = "azureAd"
    internal const val CLIENT_ID = "dp-arena-innsyn"

    internal val mockOAuth2Server: MockOAuth2Server by lazy {
        MockOAuth2Server().also { server ->
            server.start()
        }
    }

    internal fun maskinToken(app: String): String =
        mockOAuth2Server
            .issueToken(
                issuerId = AZUREAD_ISSUER_ID,
                audience = CLIENT_ID,
                claims =
                    mapOf(
                        "idtyp" to "app",
                        "azp_name" to app,
                    ),
            ).serialize()

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
            ).serialize()

    internal fun withMockAuthServerAndTestApplication(
        moduleFunction: Application.() -> Unit,
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
                moduleFunction()
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
