package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.withMockAuthServerAndTestApplication
import no.nav.dagpenger.migrering.konfigurasjon.Feature

class ArenaMigreringApiSpec :
    StringSpec({

        "skal starte migrering av sak når feature er aktivert" {

            withMockAuthServerAndTestApplication {
                unleash.enable(Feature.START_MIGRERING.navn)
                rapid.reset()
                autentisert(
                    httpMethod = HttpMethod.Post,
                    endepunkt = "/arena/migrering/sak/15603478",
                ).apply {
                    status shouldBe HttpStatusCode.OK
                    val body = body<StartMigreringResponse>()
                    body.opplysninger?.size shouldBe 15
                    rapid.inspektør.size shouldBe 1
                }
            }
        }

        "skal ikke starte migrering av sak når feature er deaktivert" {

            withMockAuthServerAndTestApplication {
                unleash.disable(Feature.START_MIGRERING.navn)
                rapid.reset()
                autentisert(
                    httpMethod = HttpMethod.Post,
                    endepunkt = "/arena/migrering/sak/15603478",
                ).apply {
                    status shouldBe HttpStatusCode.Forbidden
                    rapid.inspektør.size shouldBe 0
                }
            }
        }
    })
