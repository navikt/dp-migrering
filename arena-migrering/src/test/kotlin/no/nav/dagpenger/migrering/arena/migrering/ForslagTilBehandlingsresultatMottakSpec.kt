package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.assertions.json.shouldEqualJson
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
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import tools.jackson.databind.json.JsonMapper
import java.util.UUID

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
            val mapper = JsonMapper.builder().build()
            val forventet = mapper.readTree(melding)
            val meldingId = UUID.fromString(forventet["@id"].asString())

            test.rapid.sendTestMessage(melding)

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    test.migreringId,
                    meldingId,
                    "FORSLAG_TIL_BEHANDLINGSRESULTAT",
                    "testident",
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                )
            }
            val lagret = mapper.readTree(test.lagretHendelse.captured)
            forventet
                .properties()
                .filterNot { it.key in setOf("system_read_count", "system_participating_services") }
                .forEach { (felt, verdi) ->
                    lagret[felt].toString() shouldEqualJson verdi.toString()
                }
            test.rapid.inspektør.size shouldBe 0
            confirmVerified(test.repository)
        }

        "ignorerer andre hendelsesnavn" {
            val test = ForslagTilBehandlingsresultatTest()

            test.rapid.sendTestMessage(test.melding(eventName = "behandling_opprettet"))

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "ignorerer forslag som ikke gjelder migrering" {
            val test = ForslagTilBehandlingsresultatTest()

            test.rapid.sendTestMessage(test.melding(hendelseType = "Søknad"))

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        listOf("behandletHendelse", "behandletHendelse.type", "behandletHendelse.id", "behandlingId").forEach { felt ->
            "ignorerer meldinger uten $felt" {
                val test = ForslagTilBehandlingsresultatTest()

                test.rapid.sendTestMessage(test.melding(utenFelt = felt))

                verify { test.repository wasNot Called }
                test.rapid.inspektør.size shouldBe 0
            }
        }

        "behandler ikke meldingen når start migrering er deaktivert" {
            val test = ForslagTilBehandlingsresultatTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(test.melding())

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }
    })

private class ForslagTilBehandlingsresultatTest {
    val rapid = TestRapid()
    val repository = mockk<MigreringRepository>()
    val migreringId = UUID.randomUUID()
    val behandlingId = UUID.randomUUID()
    val lagretHendelse = slot<String>()

    init {
        every { repository.lagreMigreringHendelse(any(), any(), any(), any(), capture(lagretHendelse), any()) } just Runs
        ForslagTilBehandlingsresultatMottak(
            rapidsConnection = rapid,
            migreringMediator =
                MigreringMediator(
                    arenaRepository = mockk<ArenaRepository>(),
                    migreringRepository = repository,
                    rapid = rapid,
                ),
        )
    }

    fun melding(
        eventName: String = "forslag_til_behandlingsresultat",
        hendelseType: String = "Migrering",
        utenFelt: String? = null,
    ): String =
        JsonMessage
            .newMessage(
                eventName,
                mapOf(
                    "ident" to "testident",
                    "behandlingId" to behandlingId.toString(),
                    "behandletHendelse" to
                        mapOf(
                            "type" to hendelseType,
                            "id" to migreringId.toString(),
                        ).filterKeys { "behandletHendelse.$it" != utenFelt },
                    "resultat" to mapOf("utfall" to "Innvilget"),
                ).filterKeys { it != utenFelt },
            ).toJson()
}
