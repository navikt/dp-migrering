package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaSakPersonRepositorySpec :
    StringSpec({

        val arenaSakPersonRepository = ArenaSakPersonRepository(oracleDataSource())

        "kan hente saker for en person" {
            val sakerForPerson = arenaSakPersonRepository.hentSakerForPerson(2321609)

            sakerForPerson.size shouldBe 1
        }
    })
