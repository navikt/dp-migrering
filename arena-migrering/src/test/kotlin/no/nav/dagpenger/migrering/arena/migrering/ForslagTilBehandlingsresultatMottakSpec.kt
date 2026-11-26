package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.ForslagTilBehandlingsresultatMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.KafkaMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringAvbruttMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.mottak.ForslagTilBehandlingsresultatMottak
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import no.nav.dagpenger.uuid.UUIDv7
import java.time.LocalDate

class ForslagTilBehandlingsresultatMottakSpec :
    StringSpec({
        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "lagrer forslaget som hendelse for riktig migrering uten å markere behandlingen som opprettet" {
            val test = ForslagTilBehandlingsresultatTest()
            val melding = test.melding()

            test.rapid.sendTestMessage(melding.toJson())

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId = melding.migreringId,
                    meldingId = melding.id,
                    hendelse = melding.navn,
                    ident = melding.ident,
                    jsonData = any(),
                    opprinnelse = KafkaMeldingV1.KafkaMeldingOpprinnelse.Ekstern.name,
                    tidspunkt = melding.opprettet,
                )
            }

            test.rapid.inspektør.size shouldBe 0
            confirmVerified(test.repository)
        }

        "ignorerer andre hendelsesnavn" {
            val test = ForslagTilBehandlingsresultatTest()

            test.rapid.sendTestMessage(
                MigreringAvbruttMeldingV1
                    .ny(
                        ident = "test-ident",
                        migreringId = UUIDv7.ny(),
                        sakId = 1,
                        initiertAv = "test-initiator",
                        migreringTidspunkt = LocalDate.now(),
                        simulering = false,
                        årsak = mapOf("årsak" to "test-årsak"),
                    ).toJson(),
            )

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "ignorerer forslag som ikke gjelder migrering" {
            val test = ForslagTilBehandlingsresultatTest()

            test.rapid.sendTestMessage(
                test
                    .melding()
                    .copy(
                        behandletHendelse =
                            ForslagTilBehandlingsresultatMeldingV1.BehandletHendelse(
                                id = UUIDv7.ny(),
                                type = "AnnenType",
                            ),
                    ).toJson(),
            )

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "behandler ikke meldingen når start migrering er deaktivert" {
            val test = ForslagTilBehandlingsresultatTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(test.melding().toJson())

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }
    })

private class ForslagTilBehandlingsresultatTest {
    val rapid = TestRapid()
    val repository = mockk<MigreringRepository>()
    val lagretHendelse = slot<String>()

    init {
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
        ForslagTilBehandlingsresultatMottak(
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

    fun melding() =
        ForslagTilBehandlingsresultatMeldingV1.ny(
            ident = "test-ident",
            behandlingId = UUIDv7.ny(),
            behandletHendelse =
                ForslagTilBehandlingsresultatMeldingV1.BehandletHendelse(
                    id = UUIDv7.ny(),
                    type = "Migrering",
                ),
            opplysninger = emptyList(),
        )
}
