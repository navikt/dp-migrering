package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.assertions.json.shouldBeValidJson
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.withMockAuthServerAndTestApplication

class ArenaInnsynApiSpec :
    StringSpec({

        "skal returnere gyldig JSON og 200 på sakId" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/1/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.OK
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldContainJsonKey("sakId")
                        }
                    }
                }
        }

        "skal returnere 404 hvis sakId ikke finnes" {
            ArenaInnsynSystem
                .nyttScenario {}
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/404/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.NotFound
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldContainJsonKeyValue("type", "urn:error:not_found")
                            body.shouldContainJsonKeyValue("status", 404)
                        }
                    }
                }
        }

        "skal returnere 400 hvis sakId ikke er et gyldig heltall" {
            ArenaInnsynSystem
                .nyttScenario {}
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/ikke-et-heltall/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.BadRequest
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldContainJsonKeyValue("type", "urn:error:bad_request")
                            body.shouldContainJsonKeyValue("status", 400)
                        }
                    }
                }
        }
    })
