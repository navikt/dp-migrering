package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaVedtakFaktaRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val vedtakFaktaRepository =
            ArenaVedtakFaktaRepository(
                dataSource = h2DataSourceBuilder.dataSource,
            )
        "kan hente fakta for vedtak" {

            // TODO: Legge til bedre testdata

            val faktaForVedtak = vedtakFaktaRepository.hentFaktaForVedtak(listOf(37067849))

            faktaForVedtak.size shouldBe 1
        }
    })
