package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
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
            val rapid = spyk(TestRapid())
            val test = StartMigreringTest(rapid)

            test.startMigrering()

            rapid.inspektør.size shouldBe 1
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

    suspend fun startMigrering() {
        mediator.startMigrering(
            sakId = migrering.sakId,
            initiertAv = migrering.initiertAv,
            simulering = migrering.simulering,
            migreringsTidspunkt = migrering.migreringTidspunkt,
        )
    }

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
