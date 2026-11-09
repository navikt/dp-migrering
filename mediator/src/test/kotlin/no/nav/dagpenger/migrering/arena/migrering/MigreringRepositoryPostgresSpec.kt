package no.nav.dagpenger.migrering.arena.migrering

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotliquery.queryOf
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.db.DatabaseSession
import no.nav.dagpenger.migrering.objectMapper
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
        val databaseSession = DatabaseSession(dataSource)
        val repository = MigreringRepositoryPostgres(databaseSession)

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
                    statement.executeUpdate("TRUNCATE TABLE migrering_hendelse, migrering_opplysninger, migrering")
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

            repository.lagreMigrering(migrering)

            val hentetMigrering = repository.hentMigrering(migrering.migreringId)
            hentetMigrering.shouldNotBeNull()
            hentetMigrering.migreringId shouldBe migrering.migreringId
            hentetMigrering.ident shouldBe migrering.ident
            hentetMigrering.sakId shouldBe migrering.sakId
            hentetMigrering.initiertAv shouldBe migrering.initiertAv
            hentetMigrering.opprettet.shouldNotBeNull()
        }

        "lagrer opplysninger som JSONB med migreringsstatus" {
            val migrering = nyMigrering()
            repository.lagreMigrering(migrering)
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
            hentOpplysningerForMigrering.forEach { hentet ->
                val forventet = opplysninger.single { it.uuid == hentet.opplysning.uuid }
                objectMapper.readTree(objectMapper.writeValueAsString(hentet.opplysning)) shouldBe
                    objectMapper.readTree(objectMapper.writeValueAsString(forventet))
                hentet.migrert shouldBe false
                hentet.migrertTidspunkt shouldBe null
            }
        }

        "tom opplysningsliste lagrer ingen rader" {
            val migrering = nyMigrering()
            repository.lagreMigrering(migrering)

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

        "markerer bare valgte opplysninger i riktig migrering som migrert" {
            val migrering = nyMigrering()
            val annenMigrering = nyMigrering()
            val valgt = Opplysning("Heltall", 42, null, UUID.randomUUID())
            val ikkeValgt = Opplysning("Boolsk", false, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreMigrering(annenMigrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(valgt, ikkeValgt))
            repository.lagreOpplysninger(annenMigrering.migreringId, listOf(valgt))

            repository.markereOpplysningerSomMigrert(migrering.migreringId, listOf(valgt))

            val hentet = repository.hentOpplysningerForMigrering(migrering.migreringId).associateBy { it.opplysning.uuid }
            hentet.size shouldBe 2
            hentet.getValue(valgt.uuid).migrert shouldBe true
            hentet.getValue(valgt.uuid).migrertTidspunkt.shouldNotBeNull()
            hentet.getValue(ikkeValgt.uuid) shouldBe MigreringOpplysning(ikkeValgt, false, null)
            repository.hentOpplysningerForMigrering(annenMigrering.migreringId) shouldBe
                listOf(MigreringOpplysning(valgt, false, null))
        }

        "tom liste ved markering endrer ingen opplysninger" {
            val migrering = nyMigrering()
            val opplysning = Opplysning("Heltall", 42, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(opplysning))

            repository.markereOpplysningerSomMigrert(migrering.migreringId, emptyList())

            repository.hentOpplysningerForMigrering(migrering.migreringId) shouldBe
                listOf(MigreringOpplysning(opplysning, false, null))
        }

        "henter etterspurte og tidligere migrerte opplysninger fra riktig migrering" {
            val migrering = nyMigrering()
            val annenMigrering = nyMigrering()
            val migrert = Opplysning("Migrert", 1, null, UUID.randomUUID())
            val etterspurt = Opplysning("Etterspurt", 2, null, UUID.randomUUID())
            val ogsåEtterspurt = Opplysning("Også etterspurt", 3, null, UUID.randomUUID())
            val ikkeEtterspurt = Opplysning("Ikke etterspurt", 4, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreMigrering(annenMigrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(migrert, etterspurt, ogsåEtterspurt, ikkeEtterspurt))
            repository.lagreOpplysninger(annenMigrering.migreringId, listOf(etterspurt, ikkeEtterspurt))
            repository.markereOpplysningerSomMigrert(migrering.migreringId, listOf(migrert))
            repository.markereOpplysningerSomMigrert(annenMigrering.migreringId, listOf(ikkeEtterspurt))

            val hentet =
                repository
                    .hentManglendeOpplysningerForMigrering(
                        migrering.migreringId,
                        listOf(migrert.uuid, etterspurt.uuid, ogsåEtterspurt.uuid, UUID.randomUUID()),
                    ).associateBy { it.opplysning.uuid }

            hentet.keys shouldBe setOf(migrert.uuid, etterspurt.uuid, ogsåEtterspurt.uuid)
            hentet.getValue(migrert.uuid).opplysning shouldBe migrert
            hentet.getValue(migrert.uuid).migrert shouldBe true
            hentet.getValue(migrert.uuid).migrertTidspunkt.shouldNotBeNull()
            hentet.getValue(etterspurt.uuid) shouldBe MigreringOpplysning(etterspurt, false, null)
            hentet.getValue(ogsåEtterspurt.uuid) shouldBe MigreringOpplysning(ogsåEtterspurt, false, null)
        }

        "tom forespørsel henter bare tidligere migrerte opplysninger" {
            val migrering = nyMigrering()
            val migrert = Opplysning("Migrert", 1, null, UUID.randomUUID())
            val ikkeMigrert = Opplysning("Ikke migrert", 2, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(migrert, ikkeMigrert))
            repository.markereOpplysningerSomMigrert(migrering.migreringId, listOf(migrert))

            val hentet = repository.hentManglendeOpplysningerForMigrering(migrering.migreringId, emptyList())

            hentet.single().opplysning shouldBe migrert
            hentet.single().migrert shouldBe true
            hentet.single().migrertTidspunkt.shouldNotBeNull()
        }

        "henter ingen manglende opplysninger for en ukjent migrering" {
            repository.hentManglendeOpplysningerForMigrering(UUID.randomUUID(), listOf(UUID.randomUUID())) shouldBe emptyList()
            repository.hentManglendeOpplysningerForMigrering(UUID.randomUUID(), emptyList()) shouldBe emptyList()
        }

        "lagrer behandlings-ID bare på riktig migrering" {
            val migrering = nyMigrering()
            val annenMigrering = nyMigrering()
            val behandlingId = UUID.randomUUID()
            repository.lagreMigrering(migrering)
            repository.lagreMigrering(annenMigrering)

            repository.markereBehandlingOpprettet(migrering.migreringId, behandlingId)

            val behandlinger =
                databaseSession.session { session ->
                    session
                        .run(
                            queryOf("SELECT migrering_id, behandling_id FROM migrering")
                                .map { it.uuid("migrering_id") to it.uuidOrNull("behandling_id") }
                                .asList,
                        ).toMap()
                }
            behandlinger shouldBe mapOf(migrering.migreringId to behandlingId, annenMigrering.migreringId to null)
        }

        listOf("Intern", "Ekstern").forEach { opprinnelse ->
            "lagrer tidspunkt og opprinnelse=$opprinnelse, og bevarer raden ved gjentatt lagring" {
                val migrering = nyMigrering()
                val meldingId = UUID.randomUUID()
                val jsonData = """{"antall":42}"""
                repository.lagreMigrering(migrering)

                repository.lagreMigreringHendelse(
                    migrering.migreringId,
                    meldingId,
                    "MIGRERING_STARTET",
                    migrering.ident,
                    jsonData,
                    opprinnelse,
                )

                val førsteRad =
                    databaseSession.session { session ->
                        session
                            .run(
                                queryOf("SELECT hendelse, data, tidspunkt, opprinnelse FROM migrering_hendelse")
                                    .map { row ->
                                        mapOf(
                                            "hendelse" to row.string("hendelse"),
                                            "data" to row.string("data"),
                                            "tidspunkt" to row.localDateTime("tidspunkt"),
                                            "opprinnelse" to row.string("opprinnelse"),
                                        )
                                    }.asSingle,
                            ).shouldNotBeNull()
                    }
                førsteRad["hendelse"] shouldBe "MIGRERING_STARTET"
                objectMapper.readTree(førsteRad["data"] as String) shouldBe objectMapper.readTree(jsonData)
                førsteRad["opprinnelse"] shouldBe opprinnelse
                førsteRad["tidspunkt"].shouldNotBeNull()

                repository.lagreMigreringHendelse(
                    migrering.migreringId,
                    meldingId,
                    "MIGRERING_FULLFØRT",
                    migrering.ident,
                    """{"antall":43}""",
                    if (opprinnelse == "Intern") "Ekstern" else "Intern",
                )

                val rader =
                    databaseSession.session { session ->
                        session
                            .run(
                                queryOf("SELECT hendelse, data, opprinnelse FROM migrering_hendelse")
                                    .map { row ->
                                        Triple(row.string("hendelse"), row.string("data"), row.string("opprinnelse"))
                                    }.asList,
                            )
                    }
                rader.size shouldBe 1
                rader.single().first shouldBe "MIGRERING_STARTET"
                objectMapper.readTree(rader.single().second) shouldBe objectMapper.readTree(jsonData)
                rader.single().third shouldBe opprinnelse
            }
        }

        "duplikat melding-ID med annen opprinnelse overskriver ikke den opprinnelige hendelsen" {
            val migrering = nyMigrering()
            val meldingId = UUID.randomUUID()
            val jsonData = """{"antall":42}"""
            repository.lagreMigrering(migrering)
            repository.lagreMigreringHendelse(
                migrering.migreringId,
                meldingId,
                "MIGRERING_STARTET",
                migrering.ident,
                jsonData,
                "Ekstern",
            )

            repository.lagreMigreringHendelse(
                migrering.migreringId,
                meldingId,
                "migrering_fullført",
                migrering.ident,
                """{"antall":43}""",
                "Intern",
            )

            databaseSession.session { session ->
                session
                    .run(
                        queryOf("SELECT hendelse, data FROM migrering_hendelse")
                            .map { row ->
                                row.string("hendelse") shouldBe "MIGRERING_STARTET"
                                objectMapper.readTree(row.string("data")) shouldBe objectMapper.readTree(jsonData)
                            }.asList,
                    ).size shouldBe 1
            }
        }

        "avviser migreringshendelser for en migrering som ikke finnes" {
            shouldThrow<SQLException> {
                repository.lagreMigreringHendelse(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "migrering_startet",
                    "testident",
                    "{}",
                    "Ekstern",
                )
            }

            databaseSession.session { session ->
                session.run(
                    queryOf("SELECT COUNT(*) AS antall FROM migrering_hendelse").map { it.int("antall") }.asSingle,
                ) shouldBe 0
            }
        }
    })

private fun nyMigrering() =
    Migrering(
        ident = "12345678987",
        sakId = 12345,
        initiertAv = "testbruker",
        migreringTidspunkt = LocalDate.of(2026, 11, 5),
    )
