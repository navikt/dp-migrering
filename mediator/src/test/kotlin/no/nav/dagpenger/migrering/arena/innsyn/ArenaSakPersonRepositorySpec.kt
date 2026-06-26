package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaSakPersonRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaSakPersonRepository = ArenaSakPersonRepository(h2DataSourceBuilder.dataSource)

        "kan hente saker for en person" {
            val sakerForPerson = arenaSakPersonRepository.hentSakerForPerson("12312312312")

            sakerForPerson.size shouldBe 1
        }
    })
