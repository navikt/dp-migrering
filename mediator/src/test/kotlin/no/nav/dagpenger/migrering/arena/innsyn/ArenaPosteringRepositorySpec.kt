package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaPosteringRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaPosteringRepository = ArenaPosteringRepository(h2DataSourceBuilder.dataSource)

        "kan hente siste utbetalingsdato for person" {

            // TODO: Mangler testdata
            val sisteUtbetalingDato = arenaPosteringRepository.sisteUtbetalingDato(PersonId(id = 103))

            sisteUtbetalingDato shouldBe null
        }
    })
