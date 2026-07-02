package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.assertions.json.shouldBeJsonArray
import io.kotest.assertions.json.shouldBeValidJson
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.withMockAuthServerAndTestApplication

class ArenaInnsynApiSpec :
    StringSpec({

        "hent person id for person skal returnere gyldig JSON og status 200" {
            ArenaInnsynSystem
                .nyttScenario { }
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            httpMethod = HttpMethod.Post,
                            endepunkt = "/arena/innsyn/person",
                            body = """{"ident":"12312312312"}""",
                        ).apply {
                            status shouldBe HttpStatusCode.OK
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldContainJsonKey("id")
                        }
                    }
                }
        }
        "hent saker for person skal returnere gyldig JSON og status 200" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            httpMethod = HttpMethod.Get,
                            endepunkt = "/arena/innsyn/sak/person/4873545",
                        ).apply {
                            status shouldBe HttpStatusCode.OK
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                            body.shouldBeJsonArray()
                        }
                    }
                }
        }

        "hent saker for person med ugyldig ident skal returnere 400 og problem-json" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            httpMethod = HttpMethod.Get,
                            endepunkt = "/arena/innsyn/sak/person/12432",
                        ).apply {
                            status shouldBe HttpStatusCode.NotFound
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                        }
                    }
                }
        }

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
                            status shouldBe HttpStatusCode.UnprocessableEntity
                            val body = bodyAsText()
                            body.shouldBeValidJson()
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
                            status shouldBe HttpStatusCode.UnprocessableEntity
                            val body = bodyAsText()
                            body.shouldBeValidJson()
                        }
                    }
                }
        }
    })
