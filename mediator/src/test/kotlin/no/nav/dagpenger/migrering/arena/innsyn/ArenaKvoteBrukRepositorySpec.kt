package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaKvoteBrukRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val kvoteBrukRepository =
            ArenaKvoteBrukRepository(
                dataSource = h2DataSourceBuilder.dataSource,
            )

        "kan hente kvotebruk hendelse for person" {

            val kvoteBrukHendelserForPerson =
                kvoteBrukRepository.hentKvoteBrukHendelserForPerson(PersonId(id = 4873545))

            kvoteBrukHendelserForPerson.size shouldBe 2
        }
    })
