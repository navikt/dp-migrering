package no.nav.dagpenger.migrering.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.MigreringsService
import no.nav.dagpenger.migrering.arena.migrering.OppfyllerKravetTilAlder
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class MigreringServiceSpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()
        val migreringsService =
            MigreringsService(
                opplysningHandler =
                    listOf(
                        OppfyllerKravetTilAlder(dataSource = h2DataSourceBuilder.dataSource),
                    ),
            )

        "skal bladi bladi" {
            val opplysninger = migreringsService.startMigrering("vedtakId")

            opplysninger.size shouldBe 1
        }
    })
