package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaVilkårsvurderingRepositorySpec :
    StringSpec({

        val vilkårsvurderingRepository =
            ArenaVilkårsvurderingRepository(
                oracleDataSource(),
            )
        "kan hente vilkårsvurdering for vedtaksIder" {

            val vilkårsvurderinger = vilkårsvurderingRepository.hentForVedtak(listOf(46859881))

            vilkårsvurderinger.size shouldBe 10
        }
    })
