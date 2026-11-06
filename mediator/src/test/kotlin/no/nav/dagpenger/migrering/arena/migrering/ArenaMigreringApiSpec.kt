package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.withMockAuthServerAndTestApplication
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class ArenaMigreringApiSpec :
    StringSpec({

        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        listOf(false, true).forEach { simulering ->
            "skal kunne starte migrering for sak med simulering=$simulering" {
                val mediator = mockk<MigreringMediator>()
                val migrering =
                    Migrering(
                        migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24e"),
                        ident = "12345678901",
                        sakId = 15603478,
                        initiertAv = "Z999999",
                        opprettet = LocalDateTime.of(2026, 11, 6, 10, 0),
                        simulering = simulering,
                        migreringsTidspunkt = LocalDate.of(2026, 11, 6),
                    )
                coEvery {
                    mediator.startMigrering(
                        sakId = migrering.sakId,
                        initiertAv = migrering.initiertAv,
                        simulering = simulering,
                        migreringsTidspunkt = migrering.migreringsTidspunkt,
                    )
                } returns migrering

                withMockAuthServerAndTestApplication(mediator) {
                    val response =
                        autentisert(
                            httpMethod = HttpMethod.Post,
                            endepunkt = "/arena/migrering/sak/${migrering.sakId}",
                            //language=json
                            body =
                                """
                                {
                                  "simulering": $simulering,
                                  "migreringsTidspunkt": "${migrering.migreringsTidspunkt}"
                                }
                                """.trimIndent(),
                        )

                    response.status shouldBe HttpStatusCode.Created
                    val body = response.body<StartMigreringResponse>()
                    body.sakId shouldBe migrering.sakId
                    body.migreringId shouldBe migrering.migreringId
                    body.simulering shouldBe simulering
                    body.initiertAv shouldBe migrering.initiertAv
                    body.ident shouldBe migrering.ident
                    body.migreringsTidspunkt shouldBe migrering.opprettet
                }

                coVerify(exactly = 1) {
                    mediator.startMigrering(
                        sakId = migrering.sakId,
                        initiertAv = migrering.initiertAv,
                        simulering = simulering,
                        migreringsTidspunkt = migrering.migreringsTidspunkt,
                    )
                }
            }
        }

        "skal ikke starte migrering når feature er deaktivert" {
            val mediator = mockk<MigreringMediator>()
            skruAvFeatures()

            withMockAuthServerAndTestApplication(mediator) {
                val response =
                    autentisert(
                        httpMethod = HttpMethod.Post,
                        endepunkt = "/arena/migrering/sak/15603478",
                        body = """{"simulering": false}""",
                    )

                response.status shouldBe HttpStatusCode.Forbidden
            }

            verify { mediator wasNot Called }
        }

        "skal ikke starte migrering uten autentisering" {
            val mediator = mockk<MigreringMediator>()

            withMockAuthServerAndTestApplication(mediator) {
                val response =
                    uautentisert(
                        httpMethod = HttpMethod.Post,
                        endepunkt = "/arena/migrering/sak/15603478",
                    )

                response.status shouldBe HttpStatusCode.Unauthorized
            }

            verify { mediator wasNot Called }
        }
    })
