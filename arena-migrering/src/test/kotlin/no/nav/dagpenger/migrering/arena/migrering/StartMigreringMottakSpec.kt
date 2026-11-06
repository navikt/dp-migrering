package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
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

        "ignorerer den første migrering_startet-meldingen uten manglendeOpplysninger" {
            val test = StartMigreringTest()

            test.rapid.sendTestMessage(migreringStartet(opplysninger = emptyList()))

            test.rapid.inspektør.size shouldBe 0
            verify { test.repository wasNot Called }
        }

        "svarer med manglende opplysninger når den andre tjenesten ikke har mottatt noen ennå" {
            val test = StartMigreringTest()
            val migreringId = UUID.randomUUID()
            val opplysning = Opplysning("Dagsats", 1500, LocalDate.of(2026, 11, 6), UUID.randomUUID())
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
            } returns listOf(MigreringOpplysning(opplysning, false, null))

            test.rapid.sendTestMessage(migreringStartet(migreringId, listOf(opplysning.uuid)))

            test.rapid.inspektør.size shouldBe 1
            val svar = test.rapid.inspektør.message(0)
            svar["@event_name"].asString() shouldBe "migrering_startet"
            svar["migreringId"].asString() shouldBe migreringId.toString()
            svar["sakId"].asInt() shouldBe 12345
            svar["simulering"].asBoolean() shouldBe true
            svar["initiertAv"].asString() shouldBe "testbruker"
            svar["manglendeOpplysninger"].isNull shouldBe true
            val sendteOpplysninger = svar["opplysninger"].asArray().toList()
            sendteOpplysninger.size shouldBe 1
            sendteOpplysninger.single()["uuid"].asString() shouldBe opplysning.uuid.toString()
            sendteOpplysninger.single()["navn"].asString() shouldBe opplysning.navn
            sendteOpplysninger.single()["verdi"].asInt() shouldBe opplysning.verdi
            sendteOpplysninger.single()["gyldigFraOgMed"].asString() shouldBe opplysning.gyldigFraOgMed.toString()
            verify(exactly = 1) {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(opplysning))
            }
        }

        "sender både tidligere migrerte og nye opplysninger gjennom flere svarrunder" {
            val test = StartMigreringTest()
            val migreringId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24e")
            val dagsats =
                Opplysning(
                    "Dagsats",
                    1500,
                    LocalDate.of(2026, 11, 6),
                    UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a24f"),
                )
            val samordnet =
                Opplysning("Samordnet", false, null, UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c"))
            val mapper = JsonMapper.builder().build()
            val markdown =
                StringBuilder(
                    """
                    # Migreringsflyt

                    Meldingsflyten kjøres med TestRapid og den faktiske MigreringMediator.
                    Den andre tjenesten simuleres, og repository-et mockes.
                    Dokumentet viser hele JSON-meldingen i hvert steg, inkludert transportmetadata.
                    Variable tekstverdier i transportmetadata er erstattet med `<metadata>`.

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

            val startMelding = migreringStartet(migreringId, opplysninger = emptyList())
            markdown.dokumenterMelding(
                "1. Migreringen starter uten opplysninger",
                "Den første migrering_startet-meldingen har en tom opplysningsliste. Mottaket svarer ikke.",
                mapper.readTree(startMelding),
            )
            test.rapid.sendTestMessage(startMelding)
            test.rapid.inspektør.size shouldBe 0

            val førsteForespørsel = migreringStartet(migreringId, listOf(dagsats.uuid))
            markdown.dokumenterMelding(
                "2. Den andre tjenesten etterspør dagsats",
                "Svaret har manglendeOpplysninger, men ingen opplysningsliste.",
                mapper.readTree(førsteForespørsel),
            )
            test.rapid.sendTestMessage(førsteForespørsel)
            test.rapid.inspektør.size shouldBe 1
            markdown.dokumenterMelding(
                "3. Mottaket sender dagsats",
                "Mottaket henter dagsats fra repository-et, publiserer den og markerer den som migrert.",
                test.rapid.inspektør.message(0),
            )
            test.rapid.inspektør
                .message(0)["opplysninger"]
                .asArray()
                .toList()
                .map { it["uuid"].asString() } shouldBe listOf(dagsats.uuid.toString())

            val andreForespørsel =
                migreringStartet(
                    migreringId,
                    manglendeOpplysninger = listOf(samordnet.uuid),
                    opplysninger = listOf(dagsats),
                )
            markdown.dokumenterMelding(
                "4. Den andre tjenesten etterspør samordning",
                "Svaret inneholder dagsatsen tjenesten allerede har mottatt, og etterspør samordning.",
                mapper.readTree(andreForespørsel),
            )
            test.rapid.sendTestMessage(andreForespørsel)

            test.rapid.inspektør.size shouldBe 2
            val svar = test.rapid.inspektør.message(1)
            markdown.dokumenterMelding(
                "5. Mottaket sender tidligere migrerte og nye opplysninger",
                "Repository-et returnerer både tidligere migrert dagsats og etterspurt samordning. " +
                    "Mottaket publiserer begge uten duplikater og markerer dem som migrert.",
                svar,
            )
            svar["migreringId"].asString() shouldBe migreringId.toString()
            svar["manglendeOpplysninger"].isNull shouldBe true
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
            val migreringId = UUID.randomUUID()
            val opplysning = Opplysning("Dagsats", 1500, null, UUID.randomUUID())
            every {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
            } returns listOf(MigreringOpplysning(opplysning, false, null))
            test.rapid.sendTestMessage(migreringStartet(migreringId, listOf(opplysning.uuid)))
            test.rapid.inspektør.size shouldBe 1

            test.rapid.sendTestMessage(
                test.rapid.inspektør
                    .message(0)
                    .toString(),
            )

            test.rapid.inspektør.size shouldBe 1
            verify(exactly = 1) {
                test.repository.hentManglendeOpplysningerForMigrering(migreringId, listOf(opplysning.uuid))
                test.repository.markereOpplysningerSomMigrert(migreringId, listOf(opplysning))
            }
        }

        "svarer ikke når start migrering er deaktivert" {
            val test = StartMigreringTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(migreringStartet(manglendeOpplysninger = listOf(UUID.randomUUID())))

            test.rapid.inspektør.size shouldBe 0
            verify { test.repository wasNot Called }
        }
    })

private class StartMigreringTest {
    val rapid = TestRapid()
    val repository = mockk<MigreringRepository>()

    init {
        every { repository.markereOpplysningerSomMigrert(any(), any()) } just Runs
        StartMigreringMottak(
            rapidsConnection = rapid,
            migreringMediator =
                MigreringMediator(
                    arenaRepository = mockk(),
                    migreringRepository = repository,
                    rapidsConnection = rapid,
                ),
        )
    }
}

private fun migreringStartet(
    migreringId: UUID = UUID.randomUUID(),
    manglendeOpplysninger: List<UUID>? = null,
    opplysninger: List<Opplysning<*>>? = null,
): String =
    JsonMessage
        .newMessage(
            "migrering_startet",
            buildMap {
                put("migreringId", migreringId.toString())
                put("sakId", 12345)
                put("simulering", true)
                put("initiertAv", "testbruker")
                manglendeOpplysninger?.let { put("manglendeOpplysninger", it.map(UUID::toString)) }
                opplysninger?.let {
                    put(
                        "opplysninger",
                        it.map { opplysning ->
                            mapOf(
                                "navn" to opplysning.navn,
                                "verdi" to opplysning.verdi,
                                "gyldigFraOgMed" to opplysning.gyldigFraOgMed?.toString(),
                                "uuid" to opplysning.uuid.toString(),
                            )
                        },
                    )
                }
            },
        ).toJson()

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
                    replace(navn, normaliserMetadata(verdi, metadata || navn in metadataFelter))
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
