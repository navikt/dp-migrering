package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.readUTF8Line
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.withTimeout
import no.dagpenger.stpeter.plugin.StPeterPlugin
import no.dagpenger.stpeter.plugin.TilgangAvvistException
import no.nav.dagpenger.arena.migrering.api.models.MigreringHendelseResponse
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.arena.migrering.TestApplication.withMockAuthServerAndTestApplication
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

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
                        migreringTidspunkt = LocalDate.of(2026, 11, 6),
                    )
                coEvery {
                    mediator.startMigrering(
                        sakId = migrering.sakId,
                        initiertAv = migrering.initiertAv,
                        simulering = simulering,
                        migreringsTidspunkt = migrering.migreringTidspunkt,
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
                                  "migreringsTidspunkt": "${migrering.migreringTidspunkt}"
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
                    body.migreringsTidspunkt shouldBe migrering.migreringTidspunkt
                }

                coVerify(exactly = 1) {
                    mediator.startMigrering(
                        sakId = migrering.sakId,
                        initiertAv = migrering.initiertAv,
                        simulering = simulering,
                        migreringsTidspunkt = migrering.migreringTidspunkt,
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

        "henter migrering med status etter tilgangssjekk" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migrering =
                Migrering(
                    migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24e"),
                    ident = "12345678901",
                    sakId = 15603478,
                    initiertAv = "Z999999",
                    opprettet = LocalDateTime.of(2026, 11, 6, 10, 0),
                    migreringTidspunkt = LocalDate.of(2026, 11, 7),
                    simulering = true,
                    status = "migrering_startet",
                )
            every { mediator.hentMigrering(migrering.migreringId) } returns migrering
            coEvery {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/{migreringId}",
                    any(),
                )
            } coAnswers {
                arg<suspend () -> Unit>(3).invoke()
            }

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                val response = autentisert(endepunkt = "/arena/migrering/${migrering.migreringId}")

                response.status shouldBe HttpStatusCode.OK
                val body = response.body<StartMigreringResponse>()
                body.migreringId shouldBe migrering.migreringId
                body.sakId shouldBe migrering.sakId
                body.ident shouldBe migrering.ident
                body.initiertAv shouldBe migrering.initiertAv
                body.simulering shouldBe migrering.simulering
                body.migreringsTidspunkt shouldBe migrering.migreringTidspunkt
                body.opprettetTidspunkt shouldBe migrering.opprettet
                body.status shouldBe migrering.status
            }

            verify(exactly = 1) { mediator.hentMigrering(migrering.migreringId) }
            coVerify(exactly = 1) {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/{migreringId}",
                    any(),
                )
            }
        }

        "avviser ugyldig migreringId" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                autentisert(endepunkt = "/arena/migrering/ugyldig")
                    .status shouldBe HttpStatusCode.BadRequest
            }

            verify { mediator wasNot Called }
            verify { stPeter wasNot Called }
        }

        "skal ikke hente migrering uten autentisering" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                uautentisert(endepunkt = "/arena/migrering/${UUID.randomUUID()}")
                    .status shouldBe HttpStatusCode.Unauthorized
            }

            verify { mediator wasNot Called }
            verify { stPeter wasNot Called }
        }

        "ukjent migrering gir samme avvisning som manglende persontilgang ved henting" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migreringId = UUID.randomUUID()
            every { mediator.hentMigrering(migreringId) } returns null

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                autentisert(endepunkt = "/arena/migrering/$migreringId")
                    .status shouldBe HttpStatusCode.Forbidden
            }

            verify(exactly = 1) { mediator.hentMigrering(migreringId) }
            verify { stPeter wasNot Called }
        }

        "manglende persontilgang avviser henting av migrering" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migrering = sseMigrering()
            every { mediator.hentMigrering(migrering.migreringId) } returns migrering
            coEvery {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/{migreringId}",
                    any(),
                )
            } throws
                TilgangAvvistException(
                    title = "Ingen tilgang",
                    status = HttpStatusCode.Forbidden,
                    type = URI("urn:error:forbidden"),
                    detail = "Ingen tilgang til ressursen",
                    instance = URI("/arena/migrering/${migrering.migreringId}"),
                )

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                autentisert(endepunkt = "/arena/migrering/${migrering.migreringId}")
                    .status shouldBe HttpStatusCode.Forbidden
            }

            verify(exactly = 1) { mediator.hentMigrering(migrering.migreringId) }
            coVerify(exactly = 1) {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/{migreringId}",
                    any(),
                )
            }
        }

        "henter alle migreringer for sak etter tilgangssjekk" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migrering =
                Migrering(
                    migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24e"),
                    ident = "12345678901",
                    sakId = 15603478,
                    initiertAv = "Z999999",
                    opprettet = LocalDateTime.of(2026, 11, 6, 10, 0),
                    migreringTidspunkt = LocalDate.of(2026, 11, 7),
                    simulering = true,
                )
            val migreringer =
                listOf(
                    migrering,
                    migrering.copy(
                        migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f"),
                        initiertAv = "Z999998",
                        opprettet = LocalDateTime.of(2026, 11, 8, 12, 0),
                        migreringTidspunkt = LocalDate.of(2026, 11, 9),
                        simulering = false,
                    ),
                )
            every { mediator.hentMigreringerForSak(migrering.sakId) } returns migreringer
            coEvery {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/sak/{sakId}",
                    any(),
                )
            } coAnswers {
                arg<suspend () -> Unit>(3).invoke()
            }

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                val response = autentisert(endepunkt = "/arena/migrering/sak/${migrering.sakId}")

                response.status shouldBe HttpStatusCode.OK
                val body = response.body<List<StartMigreringResponse>>()
                body.size shouldBe migreringer.size
                body.zip(migreringer).forEach { (responseMigrering, forventet) ->
                    responseMigrering.migreringId shouldBe forventet.migreringId
                    responseMigrering.sakId shouldBe forventet.sakId
                    responseMigrering.ident shouldBe forventet.ident
                    responseMigrering.initiertAv shouldBe forventet.initiertAv
                    responseMigrering.simulering shouldBe forventet.simulering
                    responseMigrering.migreringsTidspunkt shouldBe forventet.migreringTidspunkt
                    responseMigrering.opprettetTidspunkt shouldBe forventet.opprettet
                }
            }

            verify(exactly = 1) { mediator.hentMigreringerForSak(migrering.sakId) }
            coVerify(exactly = 1) {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/sak/{sakId}",
                    any(),
                )
            }
        }

        "sak uten migreringer gir tom liste" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val sakId = 15603478
            every { mediator.hentMigreringerForSak(sakId) } returns emptyList()

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                val response = autentisert(endepunkt = "/arena/migrering/sak/$sakId")
                response.status shouldBe HttpStatusCode.OK
                response.body<List<StartMigreringResponse>>() shouldBe emptyList()
            }

            verify(exactly = 1) { mediator.hentMigreringerForSak(sakId) }
            verify { stPeter wasNot Called }
        }

        "skal ikke hente migreringer for sak uten autentisering" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                uautentisert(endepunkt = "/arena/migrering/sak/15603478")
                    .status shouldBe HttpStatusCode.Unauthorized
            }

            verify { mediator wasNot Called }
            verify { stPeter wasNot Called }
        }

        "manglende persontilgang avviser henting av migreringer for sak" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migrering = sseMigrering()
            every { mediator.hentMigreringerForSak(migrering.sakId) } returns listOf(migrering)
            coEvery {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/sak/{sakId}",
                    any(),
                )
            } throws
                TilgangAvvistException(
                    title = "Ingen tilgang",
                    status = HttpStatusCode.Forbidden,
                    type = URI("urn:error:forbidden"),
                    detail = "Ingen tilgang til ressursen",
                    instance = URI("/arena/migrering/sak/${migrering.sakId}"),
                )

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                autentisert(endepunkt = "/arena/migrering/sak/${migrering.sakId}")
                    .status shouldBe HttpStatusCode.Forbidden
            }

            verify(exactly = 1) { mediator.hentMigreringerForSak(migrering.sakId) }
            coVerify(exactly = 1) {
                stPeter.vedTilgangTilPerson(
                    ident = migrering.ident,
                    token = any(),
                    originRoute = "/arena/migrering/sak/{sakId}",
                    any(),
                )
            }
        }

        "skal ikke strømme oppdateringer uten autentisering" {
            val mediator = mockk<MigreringMediator>()
            withMockAuthServerAndTestApplication(mediator) {
                uautentisert(endepunkt = "/arena/migrering/${UUID.randomUUID()}/oppdateringer")
                    .status shouldBe HttpStatusCode.Unauthorized
            }
            verify { mediator wasNot Called }
        }

        listOf("ugyldig/oppdateringer", "${UUID.randomUUID()}/oppdateringer?since=-1").forEach { path ->
            "avviser ugyldig forespørsel for oppdateringer: $path" {
                val mediator = mockk<MigreringMediator>()
                withMockAuthServerAndTestApplication(mediator) {
                    autentisert(endepunkt = "/arena/migrering/$path").status shouldBe HttpStatusCode.BadRequest
                }
                verify { mediator wasNot Called }
            }
        }

        "ukjent migrering gir samme avvisning som manglende persontilgang" {
            val mediator = mockk<MigreringMediator>()
            val migreringId = UUID.randomUUID()
            every { mediator.hentMigrering(migreringId) } returns null
            withMockAuthServerAndTestApplication(mediator) {
                autentisert(endepunkt = "/arena/migrering/$migreringId/oppdateringer")
                    .status shouldBe HttpStatusCode.Forbidden
            }
            verify(exactly = 0) { mediator.hentOppdateringer(any(), any(), any()) }
        }

        "manglende persontilgang avviser strømmen før hendelser hentes" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migrering = sseMigrering()
            every { mediator.hentMigrering(migrering.migreringId) } returns migrering
            coEvery { stPeter.vedTilgangTilPerson(any(), any(), any(), any()) } throws
                TilgangAvvistException(
                    title = "Ingen tilgang",
                    status = HttpStatusCode.Forbidden,
                    type = URI("urn:error:forbidden"),
                    detail = "Ingen tilgang til ressursen",
                    instance = URI("/arena/migrering/${migrering.migreringId}/oppdateringer"),
                )
            withMockAuthServerAndTestApplication(mediator, stPeter) {
                val response = autentisert(endepunkt = "/arena/migrering/${migrering.migreringId}/oppdateringer")
                response.status shouldBe HttpStatusCode.Forbidden
            }
            verify(exactly = 0) { mediator.hentOppdateringer(any(), any(), any()) }
        }

        listOf(null, "7").forEach { lastEventId ->
            "strømmer hendelser fra cursor ${lastEventId ?: "0"} etter tilgangssjekk" {
                val mediator = mockk<MigreringMediator>()
                val stPeter = mockk<StPeterPlugin>()
                val migrering = sseMigrering()
                val cursor = lastEventId?.toLong() ?: 0L
                every { mediator.hentMigrering(migrering.migreringId) } returns migrering
                every { mediator.hentOppdateringer(migrering.migreringId, cursor, 1) } returns
                    listOf(
                        MigreringHendelse(
                            id = 8,
                            migreringId = migrering.migreringId,
                            meldingId = UUID.randomUUID(),
                            sakId = migrering.sakId,
                            initiertAv = migrering.initiertAv,
                            hendelse = "migrering_startet",
                            data = """{"status":"startet"}""",
                            opprinnelse = "Intern",
                            tidspunkt = migrering.opprettet,
                            ident = migrering.ident,
                        ),
                    )
                every { mediator.hentOppdateringer(migrering.migreringId, 8, 1) } returns emptyList()
                coEvery { stPeter.vedTilgangTilPerson(any(), any(), any(), any()) } coAnswers {
                    @Suppress("UNCHECKED_CAST")
                    (invocation.args[3] as suspend () -> Unit).invoke()
                }

                withMockAuthServerAndTestApplication(mediator, stPeter) {
                    withTimeout(10_000.milliseconds) {
                        client
                            .prepareGet(
                                "/arena/migrering/${migrering.migreringId}/oppdateringer${if (lastEventId != null) "?since=3" else ""}",
                            ) {
                                bearerAuth(
                                    TestApplication.testAzureAdToken(
                                        listOf(TestApplication.SAKSBEHANDLER_GRUPPE),
                                        "Z999999",
                                    ),
                                )
                                lastEventId?.let { header("Last-Event-ID", it) }
                            }.execute { response ->
                                response.status shouldBe HttpStatusCode.OK
                                val channel = response.bodyAsChannel()
                                val lines = mutableListOf<String>()
                                while (true) {
                                    val line = channel.readUTF8Line() ?: break
                                    if (line.isEmpty()) break
                                    lines.add(line)
                                }
                                lines[0] shouldBe "event: message"
                                lines[2] shouldBe "id: 8"
                                channel.cancel(null)
                            }
                    }
                }
                verify(exactly = 1) { mediator.hentOppdateringer(migrering.migreringId, cursor, 1) }
            }
        }

        "henter migreringhendelse etter tilgangssjekk" {
            val mediator = mockk<MigreringMediator>()
            val stPeter = mockk<StPeterPlugin>()
            val migreringHendelse = migreringHendelse()
            every { mediator.hentMigreringHendelse(migreringHendelse.id) } returns migreringHendelse
            coEvery { stPeter.vedTilgangTilPerson(any(), any(), any(), any()) } coAnswers {
                @Suppress("UNCHECKED_CAST")
                (invocation.args[3] as suspend () -> Unit).invoke()
            }

            withMockAuthServerAndTestApplication(mediator, stPeter) {
                val response = autentisert(endepunkt = "/arena/migrering/hendelse/${migreringHendelse.id}")

                response.status shouldBe HttpStatusCode.OK
                val body = response.body<MigreringHendelseResponse>()
                body.id shouldBe migreringHendelse.id
                body.meldingId shouldBe migreringHendelse.meldingId
                body.sakId shouldBe migreringHendelse.sakId
                body.hendelse shouldBe migreringHendelse.hendelse
                body.tidspunkt shouldBe migreringHendelse.tidspunkt
                body.opprinnelse shouldBe migreringHendelse.opprinnelse
                body.data shouldBe migreringHendelse.data
                body.initiertAv shouldBe migreringHendelse.initiertAv
            }

            verify(exactly = 1) { mediator.hentMigreringHendelse(migreringHendelse.id) }
        }

        "avviser ugyldig hendelseId" {
            val mediator = mockk<MigreringMediator>()

            withMockAuthServerAndTestApplication(mediator) {
                autentisert(endepunkt = "/arena/migrering/hendelse/ugyldig")
                    .status shouldBe HttpStatusCode.BadRequest
            }

            verify { mediator wasNot Called }
        }

        "ukjent hendelse gir samme avvisning som manglende persontilgang" {
            val mediator = mockk<MigreringMediator>()
            val hendelseId = 123L
            every { mediator.hentMigreringHendelse(hendelseId) } returns null

            withMockAuthServerAndTestApplication(mediator) {
                autentisert(endepunkt = "/arena/migrering/hendelse/$hendelseId")
                    .status shouldBe HttpStatusCode.Forbidden
            }

            verify(exactly = 1) { mediator.hentMigreringHendelse(hendelseId) }
        }
    })

private fun sseMigrering() =
    Migrering(
        ident = "12345678901",
        sakId = 12345,
        initiertAv = "Z999999",
        migreringTidspunkt = LocalDate.of(2026, 11, 6),
    )

private fun migreringHendelse() =
    MigreringHendelse(
        id = 8,
        ident = "12345678901",
        migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24e"),
        meldingId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f"),
        sakId = 12345,
        initiertAv = "Z999999",
        hendelse = "migrering_startet",
        data = """{"status":"startet"}""",
        opprinnelse = "Intern",
        tidspunkt = LocalDateTime.of(2026, 11, 6, 10, 0),
    )
