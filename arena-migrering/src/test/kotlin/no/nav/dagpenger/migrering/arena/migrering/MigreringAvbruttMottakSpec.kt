package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringAvbruttMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.mottak.MigreringAvbruttMottak
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
import java.util.UUID

class MigreringAvbruttMottakSpec :
    StringSpec({

        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "skal håndtere melding om avbrutt migrering" {
            val testRapid = TestRapid()
            val repository = mockk<MigreringRepository>()
            MigreringAvbruttMottak(
                rapidsConnection = testRapid,
                migreringMediator =
                    MigreringMediator(
                        migreringRepository = repository,
                        arenaMigreringRepository = mockk(),
                        rapid = testRapid,
                        migreringsScope = mockk(),
                    ),
            )

            every {
                repository.lagreMigreringHendelse(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns Unit

            testRapid.sendTestMessage(
                MigreringAvbruttMeldingV1
                    .ny(
                        migreringId = UUID.randomUUID(),
                        sakId = 123456,
                        initiertAv = "Z123456",
                        ident = "123456789012",
                        årsak =
                            mapOf(
                                "kode" to "MANGELFULLE_OPPLYSNINGER",
                                "beskrivelse" to "Mangler opplysninger om inntekt",
                            ),
                        migreringTidspunkt = LocalDate.now(),
                        simulering = false,
                    ).toJson(),
            )

            testRapid.inspektør.size shouldBe 0

            verifyOrder {

                repository.lagreMigreringHendelse(
                    any(),
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
