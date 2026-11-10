package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaTelleverkRepositorySpec :
    StringSpec({

        val arenaTelleverkRepository = ArenaTelleverkRepository(oracleDataSource())

        "kan hente telleverk for person" {
            val telleverkForPerson = arenaTelleverkRepository.hentTelleverkForPerson(PersonId(id = 2321609))

            telleverkForPerson.dagpengePeriodeTeller shouldBe 1600
            telleverkForPerson.maxPeriodePermittertTellerFisk shouldBe 0
            telleverkForPerson.maxPeriodePermittertTeller shouldBe 2760
        }
    })
