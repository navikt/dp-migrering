package no.nav.dagpenger.migrering.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.ArenaRepository
import no.nav.dagpenger.migrering.arena.migrering.MedlemAvFolketrygden
import no.nav.dagpenger.migrering.arena.migrering.MigreringsOrchestrator
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class MigreringServiceSpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaRepository = ArenaRepository(h2DataSourceBuilder.dataSource)
        val migreringsService =
            MigreringsOrchestrator(
                listOf(MedlemAvFolketrygden(arenaRepository)),
            )

        "skal bladi bladi" {
            val opplysninger =
                migreringsService.initierMigrering(
                    vedtakId = 46859881,
                    sakId = 1,
                    initiertAv = "user1",
                )

            opplysninger.size shouldBe 1
        }
    })
