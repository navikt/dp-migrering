package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource
import java.time.LocalDate

class ArenaPosteringRepositorySpec :
    StringSpec({

        val arenaPosteringRepository = ArenaPosteringRepository(oracleDataSource())

        "kan hente siste utbetalingsdato for person" {

            val sisteUtbetalingDato = arenaPosteringRepository.sisteUtbetalingDato(PersonId(id = 2321609))

            sisteUtbetalingDato shouldBe LocalDate.of(2026, 8, 9)
        }
    })
