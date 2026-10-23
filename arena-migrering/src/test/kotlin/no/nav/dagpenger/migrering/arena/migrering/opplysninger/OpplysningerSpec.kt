package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OpplysningerSpec :
    StringSpec({

        "har ikke duplikate UUIDer" {
            val opplysningProdusenter = Opplysninger.produsenter
            val duplikater =
                opplysningProdusenter
                    .groupBy { it.opplysningsId }
                    .filter { it.value.size > 1 }
                    .map { it.key }

            duplikater shouldBe emptyList()
        }
    })
