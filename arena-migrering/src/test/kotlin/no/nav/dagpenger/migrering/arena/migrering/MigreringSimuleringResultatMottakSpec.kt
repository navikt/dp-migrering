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
import io.mockk.excludeRecords
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.dagpenger.migrering.arena.migrering.meldinger.Hendelse
import no.nav.dagpenger.migrering.arena.migrering.mottak.MigreringSimuleringResultatMottak
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilAlder
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.skruAvFeatures
import no.nav.dagpenger.migrering.konfigurasjon.skruPåFeature
import tools.jackson.databind.json.JsonMapper
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
            val mapper = JsonMapper.builder().build()
            val forventet = mapper.readTree(melding)
            val meldingId = UUID.fromString(forventet["@id"].asString())

            test.rapid.sendTestMessage(melding)

            verify(exactly = 1) {
                test.repository.lagreMigreringHendelse(
                    test.migreringId,
                    meldingId,
                    "MIGRERING_SIMULERING_RESULTAT",
                    "testident",
                    any(),
                    Hendelse.Opprinnelse.Ekstern.name,
                    any(),
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

        listOf(true, false).forEach { oppfyllerAlderskravet ->
            "verifiserer og bevarer simulerte opplysninger når alderskravet er $oppfyllerAlderskravet" {
                val test = MigreringSimuleringResultatTest()
                val produsent = OppfyllerKravetTilAlder()
                val opplysning =
                    Opplysning(
                        navn = produsent.opplysningsNavn,
                        verdi = oppfyllerAlderskravet,
                        gyldigFraOgMed = LocalDate.of(2026, 11, 1),
                        uuid = produsent.opplysningsId,
                    )
                every {
                    test.repository.hentOpplysning(test.migreringId, opplysning.uuid)
                } returns opplysning

                test.rapid.sendTestMessage(test.melding(simulerteOpplysninger = listOf(opplysning)))

                verify(exactly = 1) {
                    test.repository.hentOpplysning(test.migreringId, opplysning.uuid)
                    test.repository.lagreMigreringHendelse(
                        test.migreringId,
                        any(),
                        "MIGRERING_SIMULERING_RESULTAT",
                        "testident",
                        any(),
                        Hendelse.Opprinnelse.Ekstern.name,
                        any(),
                    )
                }
                val lagretMelding = JsonMapper.builder().build().readTree(test.lagretHendelse.captured)
                lagretMelding["simulerteOpplysninger"].toString() shouldEqualJson
                    """
                    [
                        {
                            "navn": "${produsent.opplysningsNavn}",
                            "verdi": $oppfyllerAlderskravet,
                            "gyldigFraOgMed": "2026-11-01",
                            "uuid": "${produsent.opplysningsId}"
                        }
                    ]
                    """.trimIndent()
                test.rapid.inspektør.size shouldBe 0
                confirmVerified(test.repository)
            }
        }

        "ignorerer andre hendelsesnavn" {
            val test = MigreringSimuleringResultatTest()

            test.rapid.sendTestMessage(test.melding(eventName = "migrering_startet"))

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        "ignorerer meldinger som ikke gjelder simulering" {
            val test = MigreringSimuleringResultatTest()

            test.rapid.sendTestMessage(test.melding(simulering = false))

            verify { test.repository wasNot Called }
            test.rapid.inspektør.size shouldBe 0
        }

        listOf("migreringId", "ident", "simulerteOpplysninger", "simulering").forEach { felt ->
            "ignorerer meldinger uten $felt" {
                val test = MigreringSimuleringResultatTest()

                test.rapid.sendTestMessage(test.melding(utenFelt = felt))

                verify { test.repository wasNot Called }
                test.rapid.inspektør.size shouldBe 0
            }
        }

        "behandler ikke meldingen når start migrering er deaktivert" {
            val test = MigreringSimuleringResultatTest()
            skruAvFeatures()

            test.rapid.sendTestMessage(test.melding())

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
        eventName: String = "migrering_simulering_resultat",
        simulering: Boolean = true,
        utenFelt: String? = null,
        simulerteOpplysninger: List<Opplysning<*>> = emptyList(),
    ): String {
        val melding =
            JsonMessage.newMessage(
                eventName,
                mapOf(
                    "ident" to "testident",
                    "migreringId" to migreringId.toString(),
                    "simulering" to simulering,
                    "simulerteOpplysninger" to simulerteOpplysninger,
                ),
            )
        val json =
            JsonMapper
                .builder()
                .build()
                .readTree(melding.toJson())
                .asObject()
        utenFelt?.let { json.remove(it) }
        return json.toString()
    }
}
