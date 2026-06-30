package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaPersonRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val personRepository = ArenaPersonRepository(h2DataSourceBuilder.dataSource)

        "kan slå opp person id på fødselsnummer" {
            val personId = personRepository.personId("12312312312")

            personId shouldBe 4873545
        }
    })
