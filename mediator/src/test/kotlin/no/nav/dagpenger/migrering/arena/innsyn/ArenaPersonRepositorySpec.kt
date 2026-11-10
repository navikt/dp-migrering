package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaPersonRepositorySpec :
    StringSpec({

        val personRepository = ArenaPersonRepository(oracleDataSource())

        "kan slå opp person id på fødselsnummer" {
            val personId = personRepository.personId("09208333333")

            personId shouldBe 2321609
        }
    })
