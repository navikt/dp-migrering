package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class VedtakRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val vedtakRepository =
            VedtakRepository(
                dataSource = h2DataSourceBuilder.dataSource,
            )

        "kan hente vedtak for en sak" {
            val hentVedtakForSak = vedtakRepository.hentVedtakForSak(SakId(901))

            hentVedtakForSak.size shouldBe 2
        }
    })
