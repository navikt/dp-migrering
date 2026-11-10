package no.nav.dagpenger.arena.statistikk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.dagpenger.ArenaStatistikkRepository
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaStatistikkRepositorySpec :
    StringSpec({

        val arenaStatistikkRepository = ArenaStatistikkRepository(oracleDataSource())

        "kan telle antall mennesker per rettighetstatus" {
            val antallDagpengepersoner = arenaStatistikkRepository.tellMenneskerPerRettighetstatus()

            antallDagpengepersoner[true] shouldBe 1L
            antallDagpengepersoner[false] shouldBe 0L
        }
    })
