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
import no.nav.dagpenger.migrering.arena.migrering.meldinger.Hendelse
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.BehandlingOpprettetMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringAvbruttMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.mottak.BehandlingOpprettetMottak
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import java.time.LocalDate
import java.util.UUID

class BehandlingOpprettetMottakSpec :
    StringSpec({
        beforeTest {
            skruAvFeatures()
            skruPåFeature(Feature.START_MIGRERING)
        }

        afterTest {
            skruAvFeatures()
        }

        "lagrer hendelsen og markerer behandlingen som opprettet for riktig migrering" {
            val test = BehandlingOpprettetTest()
            val melding = test.melding()

            test.rapid.sendTestMessage(melding.toJson())

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    migreringId = melding.migreringId,
                    meldingId = melding.id,
                    hendelse = melding.navn,
                    ident = melding.ident,
                    jsonData = any(),
                    opprinnelse = Hendelse.Opprinnelse.Ekstern.name,
                    tidspunkt = melding.opprettet,
                )
                test.repository.markereBehandlingOpprettet(melding.migreringId, melding.behandlingId)
            }

            test.rapid.inspektør.size shouldBe 0
            confirmVerified(test.repository)
        }

        "ignorerer andre hendelsesnavn" {
            val test = BehandlingOpprettetTest()

            test.rapid.sendTestMessage(
                MigreringAvbruttMeldingV1
                    .ny(
                        ident = "testident",
                        migreringId = UUID.randomUUID(),
                        sakId = 123456,
                        initiertAv = "MIGRERING",
                        migreringTidspunkt = LocalDate.now(),
                        simulering = false,
                        årsak = emptyMap(),
                    ).toJson(),
            )

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "ignorerer behandlinger som ikke gjelder migrering" {
            val test = BehandlingOpprettetTest()

            test.rapid.sendTestMessage(
                test
                    .melding()
                    .copy(
                        behandletHendelse =
                            BehandlingOpprettetMeldingV1.BehandletHendelse(
                                id = UUID.randomUUID(),
                                type = "AnnenType",
                            ),
                    ).toJson(),
            )

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "behandler ikke meldingen når start migrering er deaktivert" {
            val test = BehandlingOpprettetTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(test.melding().toJson())

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }
    })

private class BehandlingOpprettetTest {
    val rapid = TestRapid()
    val repository = mockk<MigreringRepository>()
    val migreringId = UUID.randomUUID()
    val behandlingId = UUID.randomUUID()
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
        every { repository.markereBehandlingOpprettet(any(), any()) } just Runs
        BehandlingOpprettetMottak(
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
        BehandlingOpprettetMeldingV1.ny(
            ident = "testident",
            behandlingId = behandlingId,
            behandletHendelse =
                BehandlingOpprettetMeldingV1.BehandletHendelse(
                    id = migreringId,
                    type = "Migrering",
                ),
        )
}
