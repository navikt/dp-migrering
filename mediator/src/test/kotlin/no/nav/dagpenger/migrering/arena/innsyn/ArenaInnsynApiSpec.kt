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

        "hent på sakId skal returnere gyldig JSON og status 200" {
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

        "hent på sakId skal returnere 404 hvis ikke sakId finnes" {
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

        "hent på sakId skal returnere 400 hvis ikke sakId er et gyldig heltall" {
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

        "hent på saksnummer skal returnere gyldig JSON og status 200" {

            ArenaInnsynSystem
                .nyttScenario {}
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/2021/1/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.OK
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldContainJsonKey("opprettetAar")
                            body.shouldContainJsonKey("lopenr")
                        }
                    }
                }
        }

        "hent på saksnummer skal returnere 404 hvis ikke saksnummer finnes" {
            ArenaInnsynSystem
                .nyttScenario {}
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/2030/1234/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.NotFound
                            val body = bodyAsText()
                            body.shouldContainJsonKeyValue("type", "urn:error:not_found")
                            body.shouldContainJsonKeyValue("status", 404)
                        }
                    }
                }
        }

        "hent på saksnummer skal returnere 400 hvis ikke aar og lopenummer er et gyldig heltall" {
            ArenaInnsynSystem
                .nyttScenario {}
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/tyvetretti/entotrefire/detaljert",
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
