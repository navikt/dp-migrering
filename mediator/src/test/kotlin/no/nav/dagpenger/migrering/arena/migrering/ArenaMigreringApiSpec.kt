package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.arena.migrering.api.models.MigreringGrunnlagDTO
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.withMockAuthServerAndTestApplication
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate

class ArenaMigreringApiSpec :
    StringSpec({

        "skal kunne hente migreringsgrunnlag for sak" {

            withMockAuthServerAndTestApplication {
                skruPåFeature(Feature.START_MIGRERING)
                autentisert(
                    httpMethod = HttpMethod.Get,
                    endepunkt = "/arena/migrering/sak/15603478",
                ).apply {
                    status shouldBe HttpStatusCode.OK
                    val body = body<MigreringGrunnlagDTO>()
                    body.opplysninger.size shouldBe 15
                }
            }
        }

        "skal ikke hente migreringsgrunnlag for sak når feature er deaktivert" {

            withMockAuthServerAndTestApplication {
                skruAvFeatures()
                autentisert(
                    httpMethod = HttpMethod.Get,
                    endepunkt = "/arena/migrering/sak/15603478",
                ).apply {
                    status shouldBe HttpStatusCode.Forbidden
                }
            }
        }

        "skal kunne starte migrering for sak" {

            withMockAuthServerAndTestApplication {
                skruPåFeature(Feature.START_MIGRERING)
                autentisert(
                    httpMethod = HttpMethod.Post,
                    endepunkt = "/arena/migrering/sak/15603478",
                    //language=json
                    body =
                        """
                        {
                            "sakId": 13699879,
                            "innvilgetFraOgMed": "${LocalDate.of(2026, 1, 5)}",
                            "innvilgetTilOgMed": null,
                            "initiertAv": "Z990798",
                            "ident": "12345678987",
                            "opplysninger": [
                              {
                                "verdi": 1500,
                                "gyldigFraOgMed": "2024-10-04",
                                "uuid": "0194881f-9428-74d5-b160-f63a4c61a24f"
                              },
                              {
                                "verdi": 0,
                                "gyldigFraOgMed": "2024-10-04",
                                "uuid": "0194881f-9428-74d5-b160-f63a4c61a23c"
                              },
                              {
                                "verdi": 37.5,
                                "gyldigFraOgMed": "2024-10-04",
                                "uuid": "0194881f-9435-72a8-b1ce-9575cbc2a76a"
                              }
                            ]
                        }
                        """.trimIndent(),
                ).apply {
                    status shouldBe HttpStatusCode.Accepted
                    rapid.inspektør.size shouldBe 1
                }
            }
        }
    })
