package no.nav.dagpenger.migrering.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.MedlemAvFolketrygden
import no.nav.dagpenger.migrering.arena.migrering.MigreringsService
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class MigreringServiceSpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()
        val migreringsService =
            MigreringsService(
                opplysningHandler =
                    listOf(
                        MedlemAvFolketrygden(dataSource = h2DataSourceBuilder.dataSource),
                    ),
            )

        "skal bladi bladi" {
            val opplysninger = migreringsService.startMigrering(46859881)

            opplysninger.size shouldBe 1
        }
    })
