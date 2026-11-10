package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaVedtakFaktaRepositorySpec :
    StringSpec({

        val vedtakFaktaRepository =
            ArenaVedtakFaktaRepository(
                oracleDataSource(),
            )
        "kan hente fakta for vedtak" {

            // TODO: Legge til bedre testdata

            val faktaForVedtak = vedtakFaktaRepository.hentFaktaForVedtak(listOf(46859881))

            faktaForVedtak.size shouldBe 79
        }
    })
