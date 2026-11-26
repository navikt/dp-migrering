package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDate
import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.inntekt.v1.InntektKlasse
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringManglendeOpplysningerMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.mottak.MigreringManglendeOpplysningerMottak
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

class MigreringManglendeOpplysningerMottakSpec :
    StringSpec({
        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "skal motta melding om migrering med manglende opplysninger og publisere melding med opplysninger" {
            val testRapid = spyk(TestRapid())
            val test = MigreringManglendeOpplysningerTest(rapid = testRapid)

            val melding =
                MigreringManglendeOpplysningerMeldingV1.fra(
                    packet =
                        JsonMessage.newMessage(
                            "migrering_manglende_opplysninger",
                            mapOf(
                                "ident" to "12345678901",
                                "sakId" to 13820850,
                                "migreringId" to "f3e1c8d0-5b6a-4c9e-9f1e-2b6a5c9e9f1e",
                                "initiertAv" to "testident",
                                "simulering" to false,
                                "manglendeOpplysninger" to listOf(test.manglendeOpplysning.opplysning),
                                "migreringTidspunkt" to LocalDate.now().toString(),
                                "opplysninger" to test.alleredeMigrerteOpplysninger.map { it.opplysning },
                            ),
                        ),
                )
            testRapid.sendTestMessage(melding.toJson())

            testRapid.inspektør.size shouldBe 1
            val sendtMelding = testRapid.inspektør.message(0)

            sendtMelding["@event_name"].asString() shouldBe "migrering_opplysninger"
            sendtMelding["opplysninger"].isArray shouldBe true
            sendtMelding["opplysninger"].size() shouldBe 3
            sendtMelding["manglendeOpplysninger"] shouldBe null
            sendtMelding["migreringTidspunkt"].asLocalDate() shouldBe melding.migreringTidspunkt
            sendtMelding["simulering"].asBoolean() shouldBe melding.simulering

            verify {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
                test.rapid.publish(any<String>())
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
                test.repository.markereOpplysningerSomMigrert(
                    any(),
                    any(),
                )
            }
        }

        "skal publisere migrering avbrutt melding dersom vi ikke har alle nødvendige opplysninger" {
            val testRapid = spyk(TestRapid())
            val test = MigreringManglendeOpplysningerTest(rapid = testRapid)

            val melding =
                MigreringManglendeOpplysningerMeldingV1.fra(
                    packet =
                        JsonMessage.newMessage(
                            "migrering_manglende_opplysninger",
                            mapOf(
                                "ident" to "12345678901",
                                "sakId" to 13820850,
                                "migreringId" to "f3e1c8d0-5b6a-4c9e-9f1e-2b6a5c9e9f1e",
                                "initiertAv" to "testident",
                                "simulering" to false,
                                "manglendeOpplysninger" to listOf(test.opplysningSomIkkeFinnes),
                                "migreringTidspunkt" to LocalDate.now().toString(),
                                "opplysninger" to test.alleredeMigrerteOpplysninger.map { it.opplysning },
                            ),
                        ),
                )

            testRapid.sendTestMessage(melding.toJson())

            testRapid.inspektør.size shouldBe 1
            val sendtMelding = testRapid.inspektør.message(0)

            sendtMelding["@event_name"].asString() shouldBe "migrering_avbrutt"
            sendtMelding["årsak"].asObject()["kode"].asString() shouldBe "FINNER_IKKE_MANGLENDE_OPPLYSNINGER"
            sendtMelding["årsak"].asObject()["beskrivelse"].asString() shouldContain "Opplysning 4"

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }

            verify(exactly = 0) { test.repository.markereOpplysningerSomMigrert(any(), any()) }
        }
    })

private class MigreringManglendeOpplysningerTest(
    val rapid: TestRapid = TestRapid(),
) {
    private val arenaMigreringRepository = mockk<ArenaMigreringRepository>()
    val repository = mockk<MigreringRepository>()
    val mediator =
        MigreringMediator(
            arenaMigreringRepository = arenaMigreringRepository,
            migreringRepository = repository,
            rapid = rapid,
            migreringsScope = mockk(),
        )

    val opplysningId1 = UUID.randomUUID()
    val opplysningId2 = UUID.randomUUID()
    val opplysningId3 = UUID.randomUUID()
    private val manglendeVerdi: String? = null

    val opplysningSomIkkeFinnes =
        Opplysning(
            navn = "Opplysning 4",
            verdi = manglendeVerdi,
            gyldigFraOgMed = null,
            uuid = UUID.randomUUID(),
        )
    val manglendeOpplysning =
        MigreringOpplysning(
            opplysning =
                Opplysning(
                    navn = "Opplysning 1",
                    verdi = manglendeVerdi,
                    gyldigFraOgMed = null,
                    uuid = opplysningId1,
                ),
            migrert = false,
            migrertTidspunkt = null,
        )
    val alleredeMigrerteOpplysninger =
        listOf(
            MigreringOpplysning(
                opplysning =
                    Opplysning(
                        navn = "Opplysning 2",
                        verdi = LocalDate.now(),
                        gyldigFraOgMed = LocalDate.now().minusDays(1),
                        uuid = opplysningId2,
                    ),
                migrert = true,
                migrertTidspunkt = LocalDateTime.now(),
            ),
            MigreringOpplysning(
                opplysning =
                    Opplysning(
                        navn = "Opplysning 3",
                        verdi =
                            Inntekt(
                                inntektsId = "1234567890",
                                inntektsListe =
                                    listOf(
                                        KlassifisertInntektMåned(
                                            årMåned = YearMonth.now(),
                                            klassifiserteInntekter =
                                                listOf(
                                                    KlassifisertInntekt(
                                                        beløp = BigDecimal.valueOf(10000),
                                                        inntektKlasse = InntektKlasse.DAGPENGER,
                                                    ),
                                                ),
                                            harAvvik = false,
                                        ),
                                    ),
                                manueltRedigert = false,
                                begrunnelseManueltRedigert = null,
                                sisteAvsluttendeKalenderMåned = YearMonth.now(),
                                hentetTidspunkt = LocalDate.now().atStartOfDay(),
                            ),
                        gyldigFraOgMed = LocalDate.now().minusDays(1),
                        uuid = opplysningId3,
                    ),
                migrert = true,
                migrertTidspunkt = LocalDateTime.now(),
            ),
        )

    init {

        every { repository.lagreMigreringHendelse(any(), any(), any(), any(), any(), any(), any()) } just Runs
        every { repository.markereOpplysningerSomMigrert(any(), any()) } just Runs
        every { repository.hentManglendeOpplysningerForMigrering(any(), any()) } returns
            listOf(
                MigreringOpplysning(
                    opplysning =
                        Opplysning(
                            navn = "Opplysning 1",
                            verdi = 1,
                            gyldigFraOgMed = LocalDate.now().minusDays(1),
                            uuid = opplysningId1,
                        ),
                    migrert = false,
                    migrertTidspunkt = null,
                ),
            ) + alleredeMigrerteOpplysninger

        MigreringManglendeOpplysningerMottak(
            rapidsConnection = rapid,
            migreringMediator = mediator,
        )
    }
}
