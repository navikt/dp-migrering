package no.nav.dagpenger.migrering.arena.migrering

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.db.DatabaseSession
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.sql.SQLException
import java.time.LocalDate
import java.util.UUID

class MigreringRepositoryPostgresSpec :
    StringSpec({
        val postgres = PostgreSQLContainer<Nothing>("postgres:17-alpine")
        val dataSource =
            lazy {
                HikariDataSource().apply {
                    jdbcUrl = postgres.jdbcUrl
                    username = postgres.username
                    password = postgres.password
                    maximumPoolSize = 2
                }
            }
        val repository =
            MigreringRepositoryPostgres(
                databaseSession = DatabaseSession(dataSource),
            )

        beforeSpec {
            postgres.start()
            Flyway
                .configure()
                .dataSource(dataSource.value)
                .locations("classpath:flyway/migrering")
                .load()
                .migrate()
        }

        beforeTest {
            dataSource.value.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("TRUNCATE TABLE migrering_opplysninger, migrering")
                }
            }
        }

        afterSpec {
            try {
                if (dataSource.isInitialized()) {
                    dataSource.value.close()
                }
            } finally {
                postgres.stop()
            }
        }

        "lagrer migrering med alle felter i Postgres" {
            val migrering = nyMigrering()

            repository.lagre(migrering)

            val hentetMigrering = repository.hentMigrering(migrering.migreringId)
            hentetMigrering.shouldNotBeNull()
            hentetMigrering.migreringId shouldBe migrering.migreringId
            hentetMigrering.ident shouldBe migrering.ident
            hentetMigrering.sakId shouldBe migrering.sakId
            hentetMigrering.initiertAv shouldBe migrering.initiertAv
            hentetMigrering.opprettet.shouldNotBeNull()
        }

        "lagrer Opplysning direkte som JSONB med klassenavn som type" {
            val migrering = nyMigrering()
            repository.lagre(migrering)
            val gyldigFraOgMed = LocalDate.of(2026, 11, 5)
            val opplysninger =
                listOf(
                    Opplysning("Tekst", "test'\"verdi", gyldigFraOgMed, UUID.randomUUID()),
                    Opplysning("Heltall", 42, gyldigFraOgMed, UUID.randomUUID()),
                    Opplysning(
                        "Desimaltall",
                        BigDecimal("1234.56"),
                        gyldigFraOgMed,
                        UUID.randomUUID(),
                    ),
                    Opplysning("Boolsk", false, gyldigFraOgMed, UUID.randomUUID()),
                    Opplysning("Dato", gyldigFraOgMed, gyldigFraOgMed, UUID.randomUUID()),
                    Opplysning("Uten verdi", null, null, UUID.randomUUID()),
                )

            repository.lagreOpplysninger(migrering.migreringId, opplysninger)

            val hentOpplysningerForMigrering = repository.hentOpplysningerForMigrering(migrering.migreringId)

            hentOpplysningerForMigrering.size shouldBe opplysninger.size
        }

        "tom opplysningsliste lagrer ingen rader" {
            val migrering = nyMigrering()
            repository.lagre(migrering)

            repository.lagreOpplysninger(migrering.migreringId, emptyList())

            repository.hentOpplysningerForMigrering(migrering.migreringId) shouldBe emptyList()
        }

        "avviser opplysninger for en migrering som ikke finnes" {
            val migreringId = UUID.randomUUID()
            val opplysning = Opplysning("Heltall", 42, null, UUID.randomUUID())

            shouldThrow<SQLException> {
                repository.lagreOpplysninger(migreringId, listOf(opplysning))
            }

            repository.hentOpplysningerForMigrering(migreringId) shouldBe emptyList()
        }
    })

private fun nyMigrering() =
    Migrering(
        ident = "12345678987",
        sakId = 12345,
        initiertAv = "testbruker",
        migreringsTidspunkt = LocalDate.of(2026, 11, 5),
    )
