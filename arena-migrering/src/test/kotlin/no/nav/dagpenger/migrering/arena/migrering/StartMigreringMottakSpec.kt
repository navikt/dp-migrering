package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringStartetMelding
import no.nav.dagpenger.migrering.arena.migrering.mottak.MigreringStartetMottak
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
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

        "skal starte migrering" {
            runTest {
                val rapid = spyk(TestRapid())
                val test = StartMigreringTest(rapid, backgroundScope)

                val migrering = test.startMigrering()

                verify(exactly = 1) { test.repository.lagreMigrering(migrering) }
                verify(exactly = 0) {
                    test.arenaMigreringRepository.hentSakTilMigrering(any())
                    test.repository.lagreOpplysninger(any(), any())
                }
                rapid.inspektør.size shouldBe 0

                testScheduler.runCurrent()

                rapid.inspektør.size shouldBe 1
                rapid.inspektør.message(0)["migreringId"].asString() shouldBe migrering.migreringId.toString()
                verifyOrder {
                    test.repository.lagreMigrering(migrering)
                    test.arenaMigreringRepository.hentSakTilMigrering(migrering.sakId)
                    test.repository.lagreOpplysninger(migrering.migreringId, any())
                    rapid.publish(any<List<OutgoingMessage>>())
                }

                val publisertMelding = rapid.inspektør.message(0).toString()
                rapid.sendTestMessage(publisertMelding)

                verify(exactly = 1) {
                    test.repository.lagreMigrering(any())
                    test.repository.lagreOpplysninger(any(), any())
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
            }
        }

        "skal ikke starte bakgrunnsarbeid hvis lagring av migrering feiler" {
            runTest {
                val test = StartMigreringTest(migreringsScope = backgroundScope)
                every { test.repository.lagreMigrering(any()) } throws IllegalStateException("Databasefeil")

                shouldThrow<IllegalStateException> { test.startMigrering() }
                testScheduler.runCurrent()

                verify(exactly = 0) {
                    test.arenaMigreringRepository.hentSakTilMigrering(any())
                    test.repository.lagreOpplysninger(any(), any())
                }
                test.rapid.inspektør.size shouldBe 0
            }
        }

        "skal kunne starte en ny migrering etter feil i bakgrunnsarbeidet" {
            runTest {
                val migreringsScope =
                    CoroutineScope(backgroundScope.coroutineContext + SupervisorJob(backgroundScope.coroutineContext[Job]))
                val test = StartMigreringTest(migreringsScope = migreringsScope)
                every { test.arenaMigreringRepository.hentSakTilMigrering(any()) } throws
                    IllegalStateException("Kunne ikke hente opplysninger")

                test.startMigrering()
                testScheduler.runCurrent()

                migreringsScope.isActive shouldBe true
                verify(exactly = 0) { test.repository.lagreOpplysninger(any(), any()) }
                test.rapid.inspektør.size shouldBe 0

                every { test.arenaMigreringRepository.hentSakTilMigrering(any()) } returns test.sak
                val migrering = test.startMigrering()
                testScheduler.runCurrent()

                test.rapid.inspektør.size shouldBe 1
                test.rapid.inspektør
                    .message(0)["migreringId"]
                    .asString() shouldBe migrering.migreringId.toString()
            }
        }

        "skal ikke publisere startmelding hvis lagring av opplysninger feiler" {
            runTest {
                val migreringsScope =
                    CoroutineScope(backgroundScope.coroutineContext + SupervisorJob(backgroundScope.coroutineContext[Job]))
                val test = StartMigreringTest(migreringsScope = migreringsScope)
                every { test.repository.lagreOpplysninger(any(), any()) } throws IllegalStateException("Databasefeil")

                test.startMigrering()
                testScheduler.runCurrent()

                verify(exactly = 1) { test.repository.lagreOpplysninger(any(), any()) }
                test.rapid.inspektør.size shouldBe 0
                migreringsScope.isActive shouldBe true
            }
        }

        "skal kansellere bakgrunnsarbeidet når scopet stoppes" {
            runTest {
                val test = StartMigreringTest(migreringsScope = backgroundScope)

                test.startMigrering()
                backgroundScope.cancel()
                testScheduler.runCurrent()

                verify(exactly = 0) {
                    test.arenaMigreringRepository.hentSakTilMigrering(any())
                    test.repository.lagreOpplysninger(any(), any())
                }
                test.rapid.inspektør.size shouldBe 0
            }
        }

        "skal motta start migrering melding og lagre migrering hendelse" {
            val rapid = spyk(TestRapid())
            val test = StartMigreringTest(rapid)

            test.sendTestMigreringStartet()

            verifyOrder {
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
        }
    })

private class StartMigreringTest(
    val rapid: TestRapid = TestRapid(),
    migreringsScope: CoroutineScope = mockk(),
) {
    val arenaMigreringRepository = mockk<ArenaMigreringRepository>()
    val repository = mockk<MigreringRepository>()
    val mediator =
        MigreringMediator(
            arenaMigreringRepository = arenaMigreringRepository,
            migreringRepository = repository,
            rapid = rapid,
            migreringsScope = migreringsScope,
        )

    val sak =
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
            erDeks = emptyList(),
            gjenstaendeTeller = emptyList(),
            initiellTeller = emptyList(),
            aktfase = emptyList(),
        )

    init {
        every { arenaMigreringRepository.hentIdentForSak(12345) } returns "testident"

        every { arenaMigreringRepository.hentSakTilMigrering(12345) } returns sak
        every { repository.lagreMigrering(any()) } just Runs
        every { repository.lagreOpplysninger(any(), any()) } just Runs
        every { repository.lagreMigreringHendelse(any(), any(), any(), any(), any(), any(), any()) } just Runs
        every { repository.markereOpplysningerSomMigrert(any(), any()) } just Runs
        MigreringStartetMottak(
            rapidsConnection = rapid,
            migreringMediator = mediator,
        )
    }

    private val migrering =
        Migrering(
            migreringId = UUID.randomUUID(),
            ident = "testident",
            sakId = 12345,
            initiertAv = "testident",
            opprettet = LocalDateTime.now(),
            migreringTidspunkt = LocalDate.now(),
            simulering = true,
        )

    fun startMigrering(): Migrering =
        mediator.startMigrering(
            sakId = migrering.sakId,
            initiertAv = migrering.initiertAv,
            simulering = migrering.simulering,
            migreringsTidspunkt = migrering.migreringTidspunkt,
        )

    fun sendTestMigreringStartet() =
        rapid.sendTestMessage(
            MigreringStartetMelding
                .fra(
                    Migrering(
                        migreringId = UUID.randomUUID(),
                        ident = "testident",
                        sakId = 12345,
                        initiertAv = "testident",
                        opprettet = LocalDateTime.now(),
                        migreringTidspunkt = LocalDate.now(),
                        simulering = true,
                    ),
                ).toJson(),
        )
}
