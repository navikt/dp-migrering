package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaKvoteBrukRepositorySpec :
    StringSpec({

        val kvoteBrukRepository =
            ArenaKvoteBrukRepository(
                dataSource = oracleDataSource(),
            )

        "kan hente kvotebruk hendelse for person" {

            val kvoteBrukHendelserForPerson =
                kvoteBrukRepository.hentKvoteBrukHendelserForPerson(PersonId(id = 2321609))

            kvoteBrukHendelserForPerson.size shouldBe 40
        }
    })
