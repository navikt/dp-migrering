package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.withMockAuthServerAndTestApplication

class ArenaMigreringApiSpec :
    StringSpec({

        "skal..." {
            withMockAuthServerAndTestApplication {
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
    })
