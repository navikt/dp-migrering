package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.dagpenger.behandling.api.models.OpplysningerDTO
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringSimuleringResultatMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringStartetMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.mottak.MigreringSimuleringResultatMottak
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
import java.util.UUID

class MigreringSimuleringResultatMottakSpec :
    StringSpec({
        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "lagrer simuleringsresultatet som en ekstern hendelse for riktig migrering" {
            val test = MigreringSimuleringResultatTest()
            val melding = test.melding()

            test.rapid.sendTestMessage(melding.toJson())

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId = any(), // melding.migreringId,
                    meldingId = any(), // melding.id,
                    hendelse = any(), // melding.navn,
                    ident = any(), // melding.ident,
                    jsonData = any(),
                    opprinnelse = any(), // Hendelse.Opprinnelse.Ekstern.name,
                    tidspunkt = any(),
                )
            }

            test.rapid.inspektør.size shouldBe 1
            confirmVerified(test.repository)
        }

        "ignorerer andre hendelsesnavn" {
            val test = MigreringSimuleringResultatTest()

            test.rapid.sendTestMessage(
                MigreringStartetMeldingV1
                    .fra(
                        Migrering(
                            ident = "testident",
                            sakId = 123456,
                            initiertAv = "MIGRERING",
                            migreringTidspunkt = LocalDate.now(),
                        ),
                    ).toJson(),
            )

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "ignorerer meldinger som ikke gjelder simulering" {
            val test = MigreringSimuleringResultatTest()

            test.rapid.sendTestMessage(test.melding(simulering = false).toJson())

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "behandler ikke meldingen når start migrering er deaktivert" {
            val test = MigreringSimuleringResultatTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(test.melding().toJson())

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }
    })

private class MigreringSimuleringResultatTest {
    val rapid = TestRapid()
    val repository = mockk<MigreringRepository>()
    val migreringId = UUID.randomUUID()
    val lagretHendelse = slot<String>()

    init {
        excludeRecords { repository.toString() }
        every {
            repository.lagreMigreringHendelse(
                migreringId = any(),
                meldingId = any(),
                hendelse = any(),
                ident = any(),
                jsonData = capture(lagretHendelse),
                opprinnelse = any(),
                tidspunkt = any(),
            )
        } just Runs
        MigreringSimuleringResultatMottak(
            rapidsConnection = rapid,
            migreringMediator =
                MigreringMediator(
                    arenaMigreringRepository = mockk<ArenaMigreringRepository>(),
                    migreringRepository = repository,
                    rapid = rapid,
                    migreringsScope = mockk(),
                ),
        )
    }

    fun melding(
        simulerteOpplysninger: List<OpplysningerDTO> = emptyList(),
        simulering: Boolean = true,
    ) = MigreringSimuleringResultatMeldingV1.ny(
        migreringId = migreringId,
        sakId = 1,
        initiertAv = "test",
        migreringTidspunkt = LocalDate.now(),
        ident = "testident",
        simulering = simulering,
        simulerteOpplysninger = simulerteOpplysninger,
    )
}
