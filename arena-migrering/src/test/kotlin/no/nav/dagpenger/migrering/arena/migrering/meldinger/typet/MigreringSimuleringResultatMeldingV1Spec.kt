package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.jsonMessageFromResource

class MigreringSimuleringResultatMeldingV1Spec :
    StringSpec({

        "skal deserialisere migrering simulering resultat melding" {

            val melding =
                jsonMessageFromResource("/fixture/migrering_simulering_resultat.json").let {
                    MigreringSimuleringResultatMeldingV1.fra(it)
                }

            melding.ident shouldBe "12345678909"
            melding.sakId shouldBe 1
            melding.initiertAv shouldBe "Z000000"
            melding.simulering shouldBe true
        }
    })
