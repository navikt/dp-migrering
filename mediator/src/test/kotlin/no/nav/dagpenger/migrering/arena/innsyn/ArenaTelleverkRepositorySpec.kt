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
            val telleverkForPerson = arenaTelleverkRepository.hentTelleverkForPerson(PersonId(id = 4873545))

            telleverkForPerson.dagpengePeriodeTeller shouldBe 5280
            telleverkForPerson.maxPeriodePermittertTellerFisk shouldBe 460
            telleverkForPerson.maxPeriodePermittertTeller shouldBe 0
        }
    })
