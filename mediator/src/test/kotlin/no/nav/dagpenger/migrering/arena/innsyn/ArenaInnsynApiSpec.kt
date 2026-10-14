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
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.whenAllowAccessToPerson
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.whenDenyAccessToPerson
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.withMockAuthServerAndTestApplication

class ArenaInnsynApiSpec :
    StringSpec({

        "hent person id for person skal returnere gyldig JSON og status 200" {

            withMockAuthServerAndTestApplication {
                whenAllowAccessToPerson {
                    autentisert(
                        httpMethod = HttpMethod.Post,
                        endepunkt = "/arena/innsyn/person",
                        body = """{"ident":"09208333333"}""",
                    ).apply {
                        status shouldBe HttpStatusCode.OK
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKey("id")
                    }
                }
            }
        }

        "hent person id for person uten tilgang til personen skal returnere 403" {

            withMockAuthServerAndTestApplication {
                whenDenyAccessToPerson {
                    autentisert(
                        httpMethod = HttpMethod.Post,
                        endepunkt = "/arena/innsyn/person",
                        body = """{"ident":"12312312312"}""",
                    ).apply {
                        status shouldBe HttpStatusCode.Forbidden
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKeyValue("status", 403)
                        body.shouldContainJsonKeyValue("detail", "Du har ikke tilgang til personen.")
                    }
                }
            }
        }

        "hent saker for person skal returnere gyldig JSON og status 200" {

            withMockAuthServerAndTestApplication {
                whenAllowAccessToPerson {
                    autentisert(
                        httpMethod = HttpMethod.Get,
                        endepunkt = "/arena/innsyn/sak/person/2321609",
                    ).apply {
                        status shouldBe HttpStatusCode.OK
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldBeJsonArray()
                    }
                }
            }
        }

        "hent saker for person uten tilgang til personen skal returnere 403" {

            withMockAuthServerAndTestApplication {
                whenDenyAccessToPerson {
                    autentisert(
                        httpMethod = HttpMethod.Get,
                        endepunkt = "/arena/innsyn/sak/person/2321609",
                    ).apply {
                        status shouldBe HttpStatusCode.Forbidden
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKeyValue("status", 403)
                        body.shouldContainJsonKeyValue("detail", "Du har ikke tilgang til personen.")
                    }
                }
            }
        }

        "hent saker for person med ugyldig ident skal returnere 403 og problem-json" {

            withMockAuthServerAndTestApplication {
                autentisert(
                    httpMethod = HttpMethod.Get,
                    endepunkt = "/arena/innsyn/sak/person/12432",
                ).apply {
                    status shouldBe HttpStatusCode.Forbidden
                    val body = bodyAsText()
                    body.shouldBeValidJson()
                }
            }
        }

        "hent på sakId skal returnere gyldig JSON og status 200" {

            withMockAuthServerAndTestApplication {
                whenAllowAccessToPerson {
                    autentisert(
                        endepunkt = "/arena/innsyn/sak/15603478/detaljert",
                    ).apply {
                        status shouldBe HttpStatusCode.OK
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKey("sakId")
                    }
                }
            }
        }

        "hent på sakId for person uten tilgang til personen skal returnere 403" {

            withMockAuthServerAndTestApplication {
                whenDenyAccessToPerson {
                    autentisert(
                        endepunkt = "/arena/innsyn/sak/15603478/detaljert",
                    ).apply {
                        status shouldBe HttpStatusCode.Forbidden
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKeyValue("status", 403)
                        body.shouldContainJsonKeyValue("detail", "Du har ikke tilgang til personen.")
                    }
                }
            }
        }

        "hent på sakId skal returnere 403 hvis ikke sakId finnes" {

            withMockAuthServerAndTestApplication {
                whenAllowAccessToPerson {
                    autentisert(
                        endepunkt = "/arena/innsyn/sak/404/detaljert",
                    ).apply {
                        status shouldBe HttpStatusCode.Forbidden
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKeyValue("type", "urn:error:forbidden")
                        body.shouldContainJsonKeyValue("status", 403)
                    }
                }
            }
        }

        "hent på sakId skal returnere 422 hvis ikke sakId er et gyldig heltall" {

            withMockAuthServerAndTestApplication {
                autentisert(
                    endepunkt = "/arena/innsyn/sak/ikke-et-heltall/detaljert",
                ).apply {
                    status shouldBe HttpStatusCode.UnprocessableEntity
                    val body = bodyAsText()
                    body.shouldBeValidJson()
                }
            }
        }

        "hent på saksnummer skal returnere gyldig JSON og status 200" {

            withMockAuthServerAndTestApplication {
                whenAllowAccessToPerson {
                    autentisert(
                        endepunkt = "/arena/innsyn/sak/2025/374364/detaljert",
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
        "hent sak på saksnummer for person uten tilgang til personen skal returnere 403" {

            withMockAuthServerAndTestApplication {
                whenDenyAccessToPerson {
                    autentisert(
                        endepunkt = "/arena/innsyn/sak/2025/374364/detaljert",
                    ).apply {
                        status shouldBe HttpStatusCode.Forbidden
                        val body = bodyAsText()
                        body.shouldBeValidJson()
                        body.shouldContainJsonKeyValue("status", 403)
                        body.shouldContainJsonKeyValue("detail", "Du har ikke tilgang til personen.")
                    }
                }
            }
        }

        "hent på saksnummer skal returnere 403 hvis saksnummer ikke finnes" {

            withMockAuthServerAndTestApplication {
                autentisert(
                    endepunkt = "/arena/innsyn/sak/2030/1234/detaljert",
                ).apply {
                    status shouldBe HttpStatusCode.Forbidden
                    val body = bodyAsText()
                    body.shouldContainJsonKeyValue("type", "urn:error:forbidden")
                    body.shouldContainJsonKeyValue("status", 403)
                }
            }
        }

        "hent på saksnummer skal returnere 400 hvis ikke aar og lopenummer er et gyldig heltall" {

            withMockAuthServerAndTestApplication {
                autentisert(
                    endepunkt = "/arena/innsyn/sak/tyvetretti/entotrefire/detaljert",
                ).apply {
                    status shouldBe HttpStatusCode.UnprocessableEntity
                    val body = bodyAsText()
                    body.shouldBeValidJson()
                }
            }
        }
    })
