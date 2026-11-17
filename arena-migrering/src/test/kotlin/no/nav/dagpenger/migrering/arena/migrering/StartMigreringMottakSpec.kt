package no.nav.dagpenger.migrering.arena.migrering

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
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
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
                    melding["@event_name"].asString().uppercase(),
                    "testident",
                    melding.toString(),
                    Hendelse.Opprinnelse.Intern.name,
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
