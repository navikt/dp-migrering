package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import com.github.navikt.tbd_libs.rapids_and_rivers_api.FailedMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.SentMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.namer.NamerWrapper
import tools.jackson.core.util.DefaultIndenter
import tools.jackson.core.util.DefaultPrettyPrinter
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class StartMigreringMottakSpec :
    StringSpec({
        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "lagrer startmeldingen som utgående etter publisering med samme melding-ID og data" {
            val rapid = spyk(TestRapid())
            val test = StartMigreringTest(rapid)

            val migrering = test.startMigrering()

            rapid.inspektør.size shouldBe 1
            val melding = rapid.inspektør.message(0)
            val meldingId = UUID.fromString(melding["@id"].asString())
            verifyOrder {
                rapid.publish(any<List<OutgoingMessage>>())
                test.repository.lagreMigreringHendelse(
                    migrering.migreringId,
                    meldingId,
                    "MIGRERING_STARTET",
                    "testident",
                    melding.toString(),
                    Hendelse.Opprinnelse.Intern.name,
                )
            }
        }

        "lagrer ikke startmeldingen som sendt når publisering feiler" {
            val rapid = spyk(TestRapid())
            val test = StartMigreringTest(rapid)
            every { rapid.publish(any<List<OutgoingMessage>>()) } answers {
                emptyList<SentMessage>() to
                    listOf(
                        FailedMessage(
                            0,
                            firstArg<List<OutgoingMessage>>().single(),
                            IllegalStateException("Publisering feilet"),
                        ),
                    )
            }

            shouldThrow<IllegalStateException> {
                test.startMigrering()
            }

            verify(exactly = 0) {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
            }
        }

        "lagrer ikke svar som sendt eller opplysninger som migrert når publisering feiler" {
            val test = StartMigreringTest()
            val migrering = test.startMigrering()
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
            val json = svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning))
            val melding =
                MigreringStartetMelding(
                    JsonMessage(json, MessageProblems(json)),
                    Hendelse.Opprinnelse.Ekstern,
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                )
            val context = mockk<MessageContext>()
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migrering.migreringId, listOf(opplysning.uuid))
            } returns listOf(MigreringOpplysning(opplysning, false, null))
            every { context.publish(any<List<OutgoingMessage>>()) } answers {
                emptyList<SentMessage>() to
                    listOf(
                        FailedMessage(
                            0,
                            firstArg<List<OutgoingMessage>>().single(),
                            IllegalStateException("Publisering feilet"),
                        ),
                    )
            }
            clearMocks(test.repository, answers = false)

            shouldThrow<IllegalStateException> {
                test.mediator.håndter(melding, context)
            }

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
            }
            verify(exactly = 0) {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    Hendelse.Opprinnelse.Intern.name,
                )
                test.repository.markereOpplysningerSomMigrert(any(), any())
            }
        }

        "ignorerer den første migrering_startet-meldingen uten manglendeOpplysninger" {
            val test = StartMigreringTest()
            test.startMigrering()
            test.rapid.inspektør.size shouldBe 1
            clearMocks(test.repository, answers = false)

            test.rapid.sendTestMessage(
                test.rapid.inspektør
                    .message(0)
                    .toString(),
            )

            test.rapid.inspektør.size shouldBe 1
            verify { test.repository wasNot Called }
        }

        "svarer med manglende opplysninger når den andre tjenesten ikke har mottatt noen ennå" {
            val test = StartMigreringTest()
            val migreringId = test.startMigrering().migreringId
            val opplysning = Opplysning("Dagsats", 1500, LocalDate.of(2026, 11, 6), UUID.randomUUID())
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
            } returns listOf(MigreringOpplysning(opplysning, false, null))
            every {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    Hendelse.Opprinnelse.Intern.name,
                )
            } answers {
                test.rapid.inspektør.size shouldBe 2
            }

            test.rapid.sendTestMessage(
                svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning)),
            )

            test.rapid.inspektør.size shouldBe 2
            val svar = test.rapid.inspektør.message(1)
            svar["@id"].asString() shouldNotBe
                test.rapid.inspektør
                    .message(0)["@id"]
                    .asString()
            svar["@event_name"].asString() shouldBe "migrering_startet"
            svar["migreringId"].asString() shouldBe migreringId.toString()
            svar["sakId"].asInt() shouldBe 12345
            svar["simulering"].asBoolean() shouldBe true
            svar["initiertAv"].asString() shouldBe "testbruker"
            svar.has("manglendeOpplysninger") shouldBe false
            val sendteOpplysninger = svar["opplysninger"].asArray().toList()
            sendteOpplysninger.size shouldBe 1
            sendteOpplysninger.single()["uuid"].asString() shouldBe opplysning.uuid.toString()
            sendteOpplysninger.single()["navn"].asString() shouldBe opplysning.navn
            sendteOpplysninger.single()["verdi"].asInt() shouldBe opplysning.verdi
            sendteOpplysninger.single()["gyldigFraOgMed"].asString() shouldBe opplysning.gyldigFraOgMed.toString()
            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId,
                    any(),
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                    "testident",
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(opplysning))
            }
            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId,
                    UUID.fromString(svar["@id"].asString()),
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                    "testident",
                    svar.toString(),
                    Hendelse.Opprinnelse.Intern.name,
                )
            }
        }

        listOf(
            "ingen opplysninger",
            "bare én etterspurt opplysning",
            "riktig antall, men feil UUID-er",
        ).forEach { scenario ->
            "publiserer avbrudd uten å markere opplysninger som migrert når databasen returnerer $scenario" {
                val test = StartMigreringTest()
                val migreringId = test.startMigrering().migreringId
                val dagsats = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
                val samordnet = Opplysning("Samordnet", false, null, UUID.randomUUID())
                val tidligereMigrert =
                    MigreringOpplysning(
                        Opplysning("Tidligere migrert", 42, null, UUID.randomUUID()),
                        true,
                        LocalDateTime.of(2026, 11, 6, 8, 0),
                    )
                val etterspurteOpplysninger = listOf(dagsats, samordnet)
                val funnetDagsats = MigreringOpplysning(dagsats, false, null)
                val resultat =
                    when (scenario) {
                        "ingen opplysninger" -> emptyList()
                        "bare én etterspurt opplysning" -> listOf(funnetDagsats)
                        else -> listOf(funnetDagsats, tidligereMigrert)
                    }
                every {
                    test.repository.hentManglendeOpplysningerForMigrering(
                        migreringId,
                        listOf(dagsats.uuid, samordnet.uuid),
                    )
                } returns resultat
                every {
                    test.repository.lagreMigreringHendelse(
                        any(),
                        any(),
                        "MIGRERING_AVBRUTT",
                        any(),
                        any(),
                        Hendelse.Opprinnelse.Intern.name,
                    )
                } answers {
                    test.rapid.inspektør.size shouldBe 2
                }

                test.rapid.sendTestMessage(
                    svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), etterspurteOpplysninger),
                )

                test.rapid.inspektør.size shouldBe 2
                val avbrudd = test.rapid.inspektør.message(1)
                avbrudd["@event_name"].asString() shouldBe "migrering_avbrutt"
                avbrudd["migreringId"].asString() shouldBe migreringId.toString()
                avbrudd["sakId"].asInt() shouldBe 12345
                avbrudd["ident"].asString() shouldBe "testident"
                avbrudd["initiertAv"].asString() shouldBe "ukjent"
                avbrudd["årsak"]["manglendeOpplysninger"]
                    .asArray()
                    .toList()
                    .map { it.asString() }
                    .toSet() shouldBe
                    if (scenario == "ingen opplysninger") {
                        setOf(dagsats.uuid.toString(), samordnet.uuid.toString())
                    } else {
                        setOf(samordnet.uuid.toString())
                    }
                verify(exactly = 1) {
                    test.repository.lagreMigreringHendelse(
                        migreringId,
                        any(),
                        "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                        "testident",
                        any(),
                        Hendelse.Opprinnelse.Ekstern.name,
                    )
                    test.repository.hentManglendeOpplysningerForMigrering(
                        migreringId,
                        listOf(dagsats.uuid, samordnet.uuid),
                    )
                    test.repository.lagreMigreringHendelse(
                        migreringId,
                        UUID.fromString(avbrudd["@id"].asString()),
                        "MIGRERING_AVBRUTT",
                        "testident",
                        avbrudd.toString(),
                        Hendelse.Opprinnelse.Intern.name,
                    )
                }
                verify(exactly = 0) {
                    test.repository.markereOpplysningerSomMigrert(any(), any())
                }
            }
        }

        "lagrer ikke avbrudd som sendt eller opplysninger som migrert når publisering feiler" {
            val test = StartMigreringTest()
            val migreringId = test.startMigrering().migreringId
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
            val json = svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning))
            val melding =
                MigreringStartetMelding(
                    JsonMessage(json, MessageProblems(json)),
                    Hendelse.Opprinnelse.Ekstern,
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                )
            val context = mockk<MessageContext>()
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
            } returns emptyList()
            every { context.publish(any<List<OutgoingMessage>>()) } answers {
                val utgående = firstArg<List<OutgoingMessage>>().single()
                dokumentasjonMapper.readTree(utgående.body)["@event_name"].asString() shouldBe "migrering_avbrutt"
                emptyList<SentMessage>() to
                    listOf(
                        FailedMessage(
                            0,
                            utgående,
                            IllegalStateException("Publisering feilet"),
                        ),
                    )
            }
            clearMocks(test.repository, answers = false)

            shouldThrow<IllegalStateException> {
                test.mediator.håndter(melding, context)
            }

            verify(exactly = 1) {
                context.publish(any<List<OutgoingMessage>>())
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
            }
            verify(exactly = 0) {
                test.repository.markereOpplysningerSomMigrert(any(), any())
            }
        }

        "svarer når samme etterspurte UUID forekommer flere ganger og finnes i databasen" {
            val test = StartMigreringTest()
            val migreringId = test.startMigrering().migreringId
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
            every {
                test.repository.hentManglendeOpplysningerForMigrering(
                    migreringId,
                    listOf(opplysning.uuid, opplysning.uuid),
                )
            } returns listOf(MigreringOpplysning(opplysning, false, null))

            test.rapid.sendTestMessage(
                svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning, opplysning)),
            )

            test.rapid.inspektør.size shouldBe 2
            test.rapid.inspektør
                .message(1)["opplysninger"]
                .asArray()
                .toList()
                .map { it["uuid"].asString() } shouldBe listOf(opplysning.uuid.toString())
            verify(exactly = 1) {
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(opplysning))
            }
        }

        "sender både tidligere migrerte og nye opplysninger gjennom flere svarrunder" {
            val test = StartMigreringTest()
            val migrering = test.startMigrering()
            val migreringId = migrering.migreringId
            val dagsats =
                Opplysning(
                    "Dagsats",
                    1500,
                    LocalDate.of(2026, 11, 6),
                    UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f"),
                )
            val samordnet =
                Opplysning("Samordnet", false, null, UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c"))
            val markdown =
                StringBuilder(
                    """
                    # Migreringsflyt

                    Meldingsflyten kjøres med TestRapid og den faktiske MigreringMediator.
                    Den andre tjenesten simuleres, og repository-et mockes.
                    Dokumentet viser hele JSON-meldingen i hvert steg, inkludert transportmetadata.
                    Variable tekstverdier i transportmetadata er erstattet med `<metadata>`.
                    Den genererte migrerings-ID-en er erstattet med `<migreringId>`.

                    """.trimIndent(),
                ).appendLine()
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(dagsats.uuid))
            } returns listOf(MigreringOpplysning(dagsats, false, null))
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(samordnet.uuid))
            } returns
                listOf(
                    MigreringOpplysning(dagsats, true, LocalDateTime.of(2026, 11, 6, 8, 0)),
                    MigreringOpplysning(samordnet, false, null),
                )

            test.rapid.inspektør.size shouldBe 1
            val startMelding = test.rapid.inspektør.message(0)
            startMelding["@event_name"].asString() shouldBe "migrering_startet"
            startMelding["migreringId"].asString() shouldBe migreringId.toString()
            startMelding["ident"].asString() shouldBe "testident"
            startMelding["sakId"].asInt() shouldBe 12345
            startMelding["simulering"].asBoolean() shouldBe true
            startMelding["initiertAv"].asString() shouldBe "testbruker"
            startMelding["migreringTidspunkt"].asString() shouldBe "2026-11-06"
            startMelding.path("opplysninger").isEmpty shouldBe true
            markdown.dokumenterMelding(
                "1. Migreringen starter uten opplysninger",
                "Den første migrering_startet-meldingen sendes uten opplysningsliste. Mottaket svarer ikke.",
                startMelding,
            )
            test.rapid.sendTestMessage(startMelding.toString())
            test.rapid.inspektør.size shouldBe 1
            verify(exactly = 0) { test.repository.hentManglendeOpplysningerForMigrering(any(), any()) }

            val førsteForespørsel = svarMedManglendeOpplysninger(startMelding, listOf(dagsats))
            markdown.dokumenterMelding(
                "2. Den andre tjenesten etterspør dagsats",
                "Tjenesten setter manglendeOpplysninger på startmeldingen, som fortsatt mangler opplysningsliste.",
                dokumentasjonMapper.readTree(førsteForespørsel),
            )
            test.rapid.sendTestMessage(førsteForespørsel)
            test.rapid.inspektør.size shouldBe 2
            val førsteSvar = test.rapid.inspektør.message(1)
            markdown.dokumenterMelding(
                "3. Mottaket sender dagsats",
                "Mottaket henter dagsats fra repository-et, publiserer den og markerer den som migrert.",
                førsteSvar,
            )
            førsteSvar["migreringId"].asString() shouldBe migreringId.toString()
            førsteSvar.has("manglendeOpplysninger") shouldBe false
            førsteSvar["opplysninger"]
                .asArray()
                .toList()
                .map { it["uuid"].asString() } shouldBe listOf(dagsats.uuid.toString())

            val andreForespørsel =
                svarMedManglendeOpplysninger(førsteSvar, listOf(samordnet))
            markdown.dokumenterMelding(
                "4. Den andre tjenesten etterspør samordning",
                "Svaret inneholder dagsatsen tjenesten allerede har mottatt, og etterspør samordning.",
                dokumentasjonMapper.readTree(andreForespørsel),
            )
            test.rapid.sendTestMessage(andreForespørsel)

            test.rapid.inspektør.size shouldBe 3
            val svar = test.rapid.inspektør.message(2)
            markdown.dokumenterMelding(
                "5. Mottaket sender tidligere migrerte og nye opplysninger",
                "Repository-et returnerer både tidligere migrert dagsats og etterspurt samordning. " +
                    "Mottaket publiserer begge uten duplikater og markerer dem som migrert.",
                svar,
            )
            svar["migreringId"].asString() shouldBe migreringId.toString()
            svar.has("manglendeOpplysninger") shouldBe false
            val sendteOpplysninger = svar["opplysninger"].asArray().toList()
            sendteOpplysninger.map { it["uuid"].asString() } shouldBe
                listOf(dagsats.uuid.toString(), samordnet.uuid.toString())
            sendteOpplysninger[0]["verdi"].asInt() shouldBe 1500
            sendteOpplysninger[1]["verdi"].asBoolean() shouldBe false
            verify(exactly = 1) {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(dagsats.uuid))
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(samordnet.uuid))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(dagsats))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(dagsats, samordnet))
            }
            verify(exactly = 2) {
                test.repository.lagreMigreringHendelse(
                    migreringId,
                    any(),
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                    "testident",
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
            }

            val dokumentasjonPath =
                Paths
                    .get("")
                    .toAbsolutePath()
                    .parent
                    .resolve("docs")
                    .toString() + "/"
            val options =
                Options()
                    .forFile()
                    .withNamer(NamerWrapper({ "MigreringFlyt" }, { dokumentasjonPath }))
                    .forFile()
                    .withExtension(".md")
            Approvals.verify(markdown.toString(), options)
        }

        "behandler ikke sitt eget svar på nytt" {
            val test = StartMigreringTest()
            val migreringId = test.startMigrering().migreringId
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
            } returns listOf(MigreringOpplysning(opplysning, false, null))
            test.rapid.sendTestMessage(
                svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning)),
            )
            test.rapid.inspektør.size shouldBe 2

            test.rapid.sendTestMessage(
                test.rapid.inspektør
                    .message(1)
                    .toString(),
            )

            test.rapid.inspektør.size shouldBe 2
            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId,
                    any(),
                    "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER",
                    "testident",
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(opplysning))
            }
        }

        "svarer ikke når start migrering er deaktivert" {
            val test = StartMigreringTest()
            test.startMigrering()
            test.rapid.inspektør.size shouldBe 1
            clearMocks(test.repository, answers = false)
            skruAvFeatures()
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())

            test.rapid.sendTestMessage(
                svarMedManglendeOpplysninger(test.rapid.inspektør.message(0), listOf(opplysning)),
            )

            test.rapid.inspektør.size shouldBe 1
            verify { test.repository wasNot Called }
        }
    })

private class StartMigreringTest(
    val rapid: TestRapid = TestRapid(),
) {
    private val arenaMigreringRepository = mockk<ArenaMigreringRepository>()
    val repository = mockk<MigreringRepository>()
    val mediator =
        MigreringMediator(
            arenaMigreringRepository = arenaMigreringRepository,
            migreringRepository = repository,
            rapid = rapid,
        )

    init {
        every { arenaMigreringRepository.hentIdentForSak(12345) } returns "testident"
        every { arenaMigreringRepository.hentSakTilMigrering(12345) } returns
            ArenaSakMigrering(
                vilkaarsVurderinger = emptyList(),
                vedtakfakta = emptyList(),
                personopplysninger = emptyList(),
                personFodselsdatoMigrering = emptyList(),
                rettighetstype = emptyList(),
                forbrukteDagerDP = emptyList(),
                forbrukteDagerPERM = emptyList(),
                forbrukteDagerFISK = emptyList(),
                tidsbegrensetVedtak = emptyList(),
                inntekt = null,
            )
        every { repository.lagreMigrering(any()) } just Runs
        every { repository.lagreOpplysninger(any(), any()) } just Runs
        every { repository.lagreMigreringHendelse(any(), any(), any(), any(), any(), any()) } just Runs
        every { repository.markereOpplysningerSomMigrert(any(), any()) } just Runs
        StartMigreringMottak(
            rapidsConnection = rapid,
            migreringMediator = mediator,
        )
    }

    suspend fun startMigrering(): Migrering =
        mediator.startMigrering(
            sakId = 12345,
            initiertAv = "testbruker",
            simulering = true,
            migreringsTidspunkt = LocalDate.of(2026, 11, 6),
        )
}

private fun svarMedManglendeOpplysninger(
    melding: JsonNode,
    manglendeOpplysninger: List<Opplysning<*>>,
): String =
    dokumentasjonMapper
        .readTree(melding.toString())
        .asObject()
        .apply {
            replace(
                "manglendeOpplysninger",
                dokumentasjonMapper.valueToTree(
                    manglendeOpplysninger.map { opplysning ->
                        mapOf(
                            "uuid" to opplysning.uuid.toString(),
                            "navn" to opplysning.navn,
                        )
                    },
                ),
            )
        }.toString()

private fun StringBuilder.dokumenterMelding(
    tittel: String,
    beskrivelse: String,
    melding: JsonNode,
) {
    appendLine("## $tittel")
    appendLine()
    appendLine(beskrivelse)
    appendLine()
    val indenter = DefaultIndenter("  ", "\n")
    val prettyPrinter =
        DefaultPrettyPrinter()
            .withObjectIndenter(indenter)
            .withArrayIndenter(indenter)
    appendLine("```json")
    appendLine(
        dokumentasjonMapper.writer().with(prettyPrinter).writeValueAsString(normaliserMetadata(melding)),
    )
    appendLine("```")
    appendLine()
}

private val dokumentasjonMapper = JsonMapper.builder().build()
private val metadataFelter = setOf("@id", "@opprettet", "@forårsaket_av", "system_participating_services")

private fun normaliserMetadata(
    melding: JsonNode,
    metadata: Boolean = false,
): JsonNode =
    when {
        melding.isObject -> {
            dokumentasjonMapper.createObjectNode().apply {
                melding.properties().sortedBy { it.key }.forEach { (navn, verdi) ->
                    replace(
                        navn,
                        if (navn == "migreringId") {
                            dokumentasjonMapper.valueToTree<JsonNode>("<migreringId>")
                        } else {
                            normaliserMetadata(verdi, metadata || navn in metadataFelter)
                        },
                    )
                }
            }
        }

        melding.isArray -> {
            dokumentasjonMapper.createArrayNode().apply {
                melding.asArray().toList().forEach { add(normaliserMetadata(it, metadata)) }
            }
        }

        metadata && melding.isString -> {
            dokumentasjonMapper.valueToTree<JsonNode>("<metadata>")
        }

        else -> {
            melding
        }
    }
