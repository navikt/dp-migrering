package no.nav.dagpenger.migrering.arena.migrering.meldinger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningVerifisertResultat
import java.time.LocalDate
import java.util.UUID

class MigreringSimuleringKontrollResultatMeldingSpec :
    StringSpec({

        "skal lage ny melding fra MigreringSimuleringKontrollResultat" {
            val migreringSimuleringKontrollResultat =
                MigreringSimuleringKontrollResultat(
                    initiertAv = "test",
                    migreringTidspunkt = LocalDate.now(),
                    simulering = true,
                    verifisert =
                        listOf(
                            OpplysningVerifisertResultat(
                                uuid = UUID.randomUUID(),
                                navn = "opplysning 1",
                                verifisert = true,
                                dpSakVerdi = "test",
                                arenaVerdi = "test",
                            ),
                            OpplysningVerifisertResultat(
                                uuid = UUID.randomUUID(),
                                navn = "opplysning 2",
                                verifisert = false,
                                dpSakVerdi = "test",
                                arenaVerdi = "test",
                            ),
                        ),
                    ikkeVerifisert =
                        listOf(
                            OpplysningVerifisertResultat(
                                uuid = UUID.randomUUID(),
                                navn = "opplysning 3",
                                verifisert = false,
                                dpSakVerdi = "test",
                                arenaVerdi = "test",
                            ),
                        ),
                    migreringId = UUID.randomUUID(),
                    ident = "12345678901",
                    sakId = 123456,
                )

            val melding = MigreringSimuleringKontrollResultatMelding.fra(migreringSimuleringKontrollResultat)

            melding.initiertAv shouldBe migreringSimuleringKontrollResultat.initiertAv
            melding.migreringTidspunkt shouldBe migreringSimuleringKontrollResultat.migreringTidspunkt
            melding.simulering shouldBe migreringSimuleringKontrollResultat.simulering
//        melding.verifisert shouldBe migreringSimuleringKontrollResultat.verifisert
//        melding.ikkeVerifisert shouldBe migreringSimuleringKontrollResultat.ikkeVerifisert
        }
    })
