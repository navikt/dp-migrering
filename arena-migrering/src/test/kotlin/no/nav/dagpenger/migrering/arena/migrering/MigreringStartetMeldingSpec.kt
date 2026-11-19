package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringStartetMelding
import java.time.LocalDate
import java.util.UUID

class MigreringStartetMeldingSpec :
    StringSpec({
        "skal lage MIGRERING_STARTET hendelse fra melding" {
            val melding =
                MigreringStartetMelding(
                    packet =
                        JsonMessage.newMessage(
                            "migrering_startet",
                            mapOf(
                                "ident" to "12345678901",
                                "sakId" to 13820850,
                                "initiertAv" to "Z990798",
                                "simulering" to true,
                                "migreringId" to "01a1e8a0-bc93-746b-9fd6-d2aa3fd19923",
                                "migreringTidspunkt" to "2026-11-17",
                                "opplysninger" to emptyList<Any>(),
                            ),
                        ),
                )

            melding.hendelse.navn() shouldBe "MIGRERING_STARTET"
        }

        "skal kunne lage ny melding fra migrering" {
            val melding =
                MigreringStartetMelding.fra(
                    Migrering(
                        ident = "12345678901",
                        sakId = 13820850,
                        initiertAv = "Z990798",
                        simulering = true,
                        migreringId = UUID.randomUUID(),
                        migreringTidspunkt = LocalDate.now(),
                    ),
                )

            melding.navn shouldBe "migrering_startet"
            melding.ident shouldBe "12345678901"
            melding.sakId shouldBe 13820850
            melding.initiertAv shouldBe "Z990798"
            melding.simulering shouldBe true
        }
    })
