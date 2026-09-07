package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaTelleverkRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaTelleverkRepository = ArenaTelleverkRepository(h2DataSourceBuilder.dataSource)

        "kan hente telleverk for person" {
            val telleverkForPerson = arenaTelleverkRepository.hentTelleverkForPerson(PersonId(id = 2321609))

            telleverkForPerson.dagpengePeriodeTeller shouldBe 1600
            telleverkForPerson.maxPeriodePermittertTellerFisk shouldBe 0
            telleverkForPerson.maxPeriodePermittertTeller shouldBe 2760
        }
    })
