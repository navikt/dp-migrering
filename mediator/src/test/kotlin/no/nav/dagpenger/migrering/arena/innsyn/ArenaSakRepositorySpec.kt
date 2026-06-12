package no.nav.dagpenger.migrering.arena.innsyn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import no.nav.dagpenger.migrering.db.H2DataSourceBuilder

class ArenaSakRepositorySpec :
    StringSpec({

        val h2DataSourceBuilder = H2DataSourceBuilder()
        h2DataSourceBuilder.runMigration()

        val arenaSakRepository =
            ArenaSakRepository(
                dbSession = h2DataSourceBuilder.dbsession,
            )

        "kan hente en Arena sak på sakId" {

            val sak = arenaSakRepository.hentSak(SakId(902))

            sak shouldNot beNull()
            sak?.sakId shouldBe "902"
        }

        "kan hente en Arena sak på saksnummer " {

            val sak =
                arenaSakRepository.hentSak(
                    Saksnummer(
                        lopenummer = 1,
                        aar = 2020,
                    ),
                )

            sak shouldNot beNull()
        }

        "skal returnere null hvis sakId ikke finnes" {
            val sak = arenaSakRepository.hentSak(SakId(11111))

            sak shouldBe null
        }

        "skal returnere null hvis saksnummer ikke finnes" {
            val sak =
                arenaSakRepository.hentSak(
                    Saksnummer(
                        lopenummer = 11111,
                        aar = 1970,
                    ),
                )

            sak shouldBe null
        }
    })
