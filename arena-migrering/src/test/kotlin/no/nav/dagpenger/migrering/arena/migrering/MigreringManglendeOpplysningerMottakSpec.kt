package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
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

        "Skal motta melding om migrering med manglende opplysninger" {
            val testRapid = spyk(TestRapid())

            val repository = mockk<MigreringRepository>()
            MigreringManglendeOpplysningerMottak(
                rapidsConnection = testRapid,
                migreringMediator =
                    MigreringMediator(
                        arenaMigreringRepository = mockk<ArenaMigreringRepository>(),
                        migreringRepository = repository,
                        rapid = testRapid,
                    ),
            )

            val opplysningId = UUID.randomUUID()

            every { repository.lagreMigreringHendelse(any(), any(), any(), any(), any(), any()) } just Runs
            every { repository.markereOpplysningerSomMigrert(any(), any()) } just Runs
            every { repository.hentManglendeOpplysningerForMigrering(any(), any()) } returns
                listOf(
                    MigreringOpplysning(
                        opplysning =
                            Opplysning(
                                navn = "Opplysning 1",
                                verdi = 1,
                                gyldigFraOgMed = LocalDate.now().minusDays(1),
                                uuid = opplysningId,
                            ),
                        migrert = false,
                        migrertTidspunkt = null,
                    ),
                )

            val melding =
                MigreringManglendeOpplysningerMelding(
                    packet =
                        JsonMessage.newMessage(
                            "migrering_manglende_opplysninger",
                            mapOf(
                                "ident" to "12345678901",
                                "sakId" to 13820850,
                                "migreringId" to "f3e1c8d0-5b6a-4c9e-9f1e-2b6a5c9e9f1e",
                                "initiertAv" to "testident",
                                "simulering" to false,
                                "manglendeOpplysninger" to
                                    listOf(
                                        Opplysning(
                                            navn = "Opplysning 1",
                                            verdi = null,
                                            gyldigFraOgMed = null,
                                            uuid = opplysningId,
                                        ),
                                    ),
                                "opprettet" to LocalDate.now().toString(),
                                "opplysninger" to emptyList<Any>(),
                            ),
                        ),
                )
            testRapid.sendTestMessage(melding.toJson())

            testRapid.inspektør.size shouldBe 1
            val sendtMelding = testRapid.inspektør.message(0)

            sendtMelding["@event_name"].asString() shouldBe "migrering_opplysninger"
            sendtMelding["opplysninger"].isArray shouldBe true
            sendtMelding["opplysninger"].size() shouldBe 1
            sendtMelding["manglendeOpplysninger"] shouldBe null

            verifyOrder {
                repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
                testRapid.publish(any<List<OutgoingMessage>>())
                repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }
        }
    })
