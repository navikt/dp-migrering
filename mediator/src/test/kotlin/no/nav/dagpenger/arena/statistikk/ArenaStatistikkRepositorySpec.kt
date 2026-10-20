package no.nav.dagpenger.arena.statistikk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.dagpenger.ArenaStatistikkRepository
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaStatistikkRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaStatistikkRepository = ArenaStatistikkRepository(h2DataSourceBuilder.dataSource)

        "kan telle antall mennesker per rettighetstatus" {
            val antallDagpengepersoner = arenaStatistikkRepository.tellMenneskerPerRettighetstatus()

            antallDagpengepersoner[true] shouldBe 1L
            antallDagpengepersoner[false] shouldBe 0L
        }
    })
