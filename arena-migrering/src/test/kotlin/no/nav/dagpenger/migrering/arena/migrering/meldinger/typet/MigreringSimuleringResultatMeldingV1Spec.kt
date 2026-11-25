package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.jsonMessageFromResource
import java.math.BigDecimal
import java.util.UUID

class MigreringSimuleringResultatMeldingV1Spec :
    StringSpec({
        val pensjonFraOffentligTjenestepensjonsordningBelopUuid = UUID.fromString("0194881f-942e-7cb0-aa59-05ea449d88e7")

        "skal deserialisere migrering simulering resultat melding" {

            val melding =
                jsonMessageFromResource("/fixture/migrering_simulering_resultat.json").let {
                    MigreringSimuleringResultatMeldingV1.fra(it)
                }

            melding.ident shouldBe "12345678909"
            melding.sakId shouldBe 123
            melding.initiertAv shouldBe "Z000000"
            melding.simulering shouldBe true
        }

        "skal konvertere verdi til forventet opplysningstype" {
            val melding =
                jsonMessageFromResource("/fixture/migrering_simulering_resultat.json").let {
                    MigreringSimuleringResultatMeldingV1.fra(it)
                }

            val pensjonsBelop =
                melding.simulerteOpplysninger
                    .single { it.uuid == pensjonFraOffentligTjenestepensjonsordningBelopUuid }
                    .verdi

            (pensjonsBelop is BigDecimal) shouldBe true
        }
    })
