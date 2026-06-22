package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.testAzureAdToken
import no.nav.dagpenger.migrering.arena.innsyn.TestApplication.withMockAuthServerAndTestApplication

class ArenaInnsynApiSpec :
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

        "ikke autentiserte kall skal returnere 401" {
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

        "kall uten saksbehandlergruppe i claim skal returnere 401" {
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
    })
