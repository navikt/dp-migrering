package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaVilkårsvurderingRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val vilkårsvurderingRepository =
            ArenaVilkårsvurderingRepository(
                dataSource = h2DataSourceBuilder.dataSource,
            )
        "kan hente vilkårsvurdering for vedtaksIder" {

            val vilkårsvurderinger = vilkårsvurderingRepository.hentForVedtak(listOf(1234))

            vilkårsvurderinger.size shouldBe 2
        }
    })
