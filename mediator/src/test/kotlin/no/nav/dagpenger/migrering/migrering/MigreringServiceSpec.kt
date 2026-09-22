package no.nav.dagpenger.migrering.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.ArenaRepository
import no.nav.dagpenger.migrering.arena.migrering.Dagpengegrunnlag
import no.nav.dagpenger.migrering.arena.migrering.InntektPeriode1
import no.nav.dagpenger.migrering.arena.migrering.InntektPeriode2
import no.nav.dagpenger.migrering.arena.migrering.InntektPeriode3
import no.nav.dagpenger.migrering.arena.migrering.MedlemAvFolketrygden
import no.nav.dagpenger.migrering.arena.migrering.MigreringsOrchestrator
import no.nav.dagpenger.migrering.arena.migrering.OppfyllerKravetTilAlder
import no.nav.dagpenger.migrering.arena.migrering.OppfyllerKravetTilHeltidDeltid
import no.nav.dagpenger.migrering.arena.migrering.OppfyllerKravetTilMobilitet
import no.nav.dagpenger.migrering.arena.migrering.OppholdINorge
import no.nav.dagpenger.migrering.arena.migrering.Samordnet
import no.nav.dagpenger.migrering.arena.migrering.SisteAvsluttendeKalenderManed
import no.nav.dagpenger.migrering.arena.migrering.UkessatsEtterSamordning
import no.nav.dagpenger.migrering.arena.migrering.UkessatsForSamordning
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class MigreringServiceSpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaRepository = ArenaRepository(h2DataSourceBuilder.dataSource)
        val migreringsService =
            MigreringsOrchestrator(
                listOf(
                    MedlemAvFolketrygden(),
                    OppholdINorge(),
                    Dagpengegrunnlag(),
                    SisteAvsluttendeKalenderManed(),
                    OppfyllerKravetTilHeltidDeltid(),
                    OppfyllerKravetTilMobilitet(),
                    UkessatsEtterSamordning(),
                    UkessatsForSamordning(),
                    Samordnet(),
                    InntektPeriode1(),
                    InntektPeriode2(),
                    InntektPeriode3(),
                    OppfyllerKravetTilAlder(),
                ),
                repository = arenaRepository,
            )

        "skal bladi bladi" {
            val opplysninger =
                migreringsService.initierMigrering(
                    sakId = 15603478,
                    initiertAv = "user1",
                )

            opplysninger.size shouldBe 13
        }
    })
