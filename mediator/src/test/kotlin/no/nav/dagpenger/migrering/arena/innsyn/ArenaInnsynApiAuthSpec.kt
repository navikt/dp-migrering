package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.assertions.json.shouldBeValidJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.testAzureAdToken
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.withMockAuthServerAndTestApplication

class ArenaInnsynApiAuthSpec :
    StringSpec({

        "kall på rot skal returnere 200 uten body" {

            ArenaInnsynSystem
                .nyttScenario { }
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        uautentisert(endepunkt = "/arena/innsyn").apply {
                            status shouldBe HttpStatusCode.OK
                            bodyAsText().isEmpty() shouldBe true
                        }
                    }
                }
        }

        "ikke autentiserte kall på sakId skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario { }
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        uautentisert(endepunkt = "/arena/innsyn/sak/902/detaljert").apply {
                            status shouldBe HttpStatusCode.Unauthorized
                        }
                    }
                }
        }

        "kall på sakId uten saksbehandlergruppe i claim skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/902/detaljert",
                            token =
                                testAzureAdToken(
                                    ADGrupper = emptyList(),
                                    navIdent = "Z999999",
                                ),
                        ).apply {
                            status shouldBe HttpStatusCode.Unauthorized
                        }
                    }
                }
        }

        "kall med saksbehandlergruppe i claim skal returnere 200" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            endepunkt = "/arena/innsyn/sak/902/detaljert",
                        ).apply {
                            status shouldBe HttpStatusCode.OK
                        }
                    }
                }
        }

        "hente saker for person uten saksbehandlergruppe i claim skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            httpMethod = HttpMethod.Get,
                            endepunkt = "/arena/innsyn/sak/person/123",
                            token =
                                testAzureAdToken(
                                    ADGrupper = emptyList(),
                                    navIdent = "Z999999",
                                ),
                        ).apply {
                            status shouldBe HttpStatusCode.Unauthorized
                            bodyAsText().shouldBeValidJson()
                        }
                    }
                }
        }

        "uautorisert hente saker for person kall skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario { }
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        uautentisert(
                            httpMethod = HttpMethod.Get,
                            endepunkt = "/arena/innsyn/sak/person/123",
                        ).apply {
                            status shouldBe HttpStatusCode.Unauthorized
                            bodyAsText().shouldBeValidJson()
                        }
                    }
                }
        }

        "hente person id for person uten saksbehandlergruppe i claim skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario {
                }.test {
                    withMockAuthServerAndTestApplication(this.api) {
                        autentisert(
                            httpMethod = HttpMethod.Post,
                            endepunkt = "/arena/innsyn/person",
                            body = """{"ident":"11223344556"}""",
                            token =
                                testAzureAdToken(
                                    ADGrupper = emptyList(),
                                    navIdent = "Z999999",
                                ),
                        ).apply {
                            status shouldBe HttpStatusCode.Unauthorized
                            bodyAsText().shouldBeValidJson()
                        }
                    }
                }
        }

        "uautorisert hent person id for person kall skal returnere 401" {
            ArenaInnsynSystem
                .nyttScenario { }
                .test {
                    withMockAuthServerAndTestApplication(this.api) {
                        uautentisert(
                            httpMethod = HttpMethod.Post,
                            endepunkt = "/arena/innsyn/person",
                        ).apply {
                            status shouldBe HttpStatusCode.Unauthorized
                            bodyAsText().shouldBeValidJson()
                        }
                    }
                }
        }
    })
