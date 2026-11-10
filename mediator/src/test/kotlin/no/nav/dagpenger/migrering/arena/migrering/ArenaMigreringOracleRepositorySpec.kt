package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource

class ArenaMigreringOracleRepositorySpec :
    StringSpec({

        val repository = ArenaMigreringOracleRepository(oracleDataSource())

        "skal hente migreringsdata for sakId" {

            shouldNotThrowAny {

                val migreringsdata = repository.hentSakTilMigrering(sakId = 15603478)

                migreringsdata.inntekt shouldNotBe null
                migreringsdata.vilkaarsVurderinger.isNotEmpty() shouldBe true
                migreringsdata.vedtakfakta.isNotEmpty() shouldBe true
                migreringsdata.personopplysninger.isNotEmpty() shouldBe true
                migreringsdata.personFodselsdatoMigrering.isNotEmpty() shouldBe true
                migreringsdata.rettighetstype.isNotEmpty() shouldBe true
                migreringsdata.forbrukteDagerDP.isNotEmpty() shouldBe true
                migreringsdata.forbrukteDagerPERM.isNotEmpty() shouldBe true
                migreringsdata.forbrukteDagerFISK.isNotEmpty() shouldBe true
                migreringsdata.tidsbegrensetVedtak.isNotEmpty() shouldBe false
            }
        }
    })
