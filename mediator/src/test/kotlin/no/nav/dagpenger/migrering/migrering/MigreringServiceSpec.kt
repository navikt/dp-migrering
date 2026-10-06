package no.nav.dagpenger.migrering.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.ArenaRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsOrchestrator
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Dagpengegrunnlag
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Dagsats
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode2
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode3
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.MedlemAvFolketrygden
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravRegistrertArbeidssoker
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilAlder
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilHeltidDeltid
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilMobilitet
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppholdINorge
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Samordnet
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.SisteAvsluttendeKalenderManed
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.UkessatsEtterSamordning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.UkessatsForSamordning
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
                    Dagsats(),
                    OppfyllerKravRegistrertArbeidssoker(),
                ),
                repository = arenaRepository,
            )

        "skal bladi bladi" {
            val opplysninger =
                migreringsService.initierMigrering(
                    sakId = 15603478,
                    initiertAv = "user1",
                )

            opplysninger.size shouldBe 15
        }
    })
