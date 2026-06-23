package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SaksnummerSpec :
    StringSpec({

        "kan bygge saksnummer fra heltall" {
            Saksnummer.from(aar = "2023", lopenummer = "1234")?.formatert() shouldBe "2023-1234"
        }

        "skal ikke bygge saksnummer hvis det ikke er heltall" {
            Saksnummer.from(aar = "ikke-gyldig-år", lopenummer = "ikke-gyldig") shouldBe null

            Saksnummer.from(aar = "1.1", lopenummer = "2.34") shouldBe null
        }
    })
