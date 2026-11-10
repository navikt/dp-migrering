package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaVedtakRepositorySpec :
    StringSpec({

        val vedtakRepository =
            ArenaVedtakRepository(
                oracleDataSource(),
            )

        "kan hente vedtak for en sak" {
            val hentVedtakForSak = vedtakRepository.hentVedtakForSak(SakId(15603478))

            hentVedtakForSak.size shouldBe 4
        }
    })
