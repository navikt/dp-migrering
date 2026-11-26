package no.nav.dagpenger.migrering.arena.migrering

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotliquery.queryOf
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Dagsats
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Fodselsdato
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.db.DatabaseSession
import no.nav.dagpenger.migrering.objectMapper
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
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

        "kan hente migreringhendelse for gitt id" {
            val migrering = nyMigrering()
            val meldingId = UUID.randomUUID()
            val jsonData = """{"antall":42}"""
            repository.lagreMigrering(migrering)

            val tidspunkt = LocalDateTime.parse("2026-11-19T08:29:40.169510")
            repository.lagreMigreringHendelse(
                migreringId = migrering.migreringId,
                meldingId = meldingId,
                hendelse = "MIGRERING_STARTET",
                ident = migrering.ident,
                jsonData = jsonData,
                opprinnelse = "Ekstern",
                tidspunkt = tidspunkt,
            )

            val hentetHendelse = repository.hentMigreringHendels(1L)
            hentetHendelse.shouldNotBeNull()
            hentetHendelse.id shouldBe 1L
            hentetHendelse.meldingId shouldBe meldingId
            hentetHendelse.migreringId shouldBe migrering.migreringId
            hentetHendelse.sakId shouldBe migrering.sakId
            hentetHendelse.initiertAv shouldBe migrering.initiertAv
            hentetHendelse.hendelse shouldBe "MIGRERING_STARTET"
            hentetHendelse.opprinnelse shouldBe "Ekstern"
            hentetHendelse.tidspunkt shouldBe tidspunkt
            objectMapper.readTree(hentetHendelse.data) shouldBe objectMapper.readTree(jsonData)
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
            hentetMigrering.simulering shouldBe migrering.simulering
        }

        "lagrer opplysninger som JSONB med migreringsstatus" {
            val migrering = nyMigrering()
            repository.lagreMigrering(migrering)
            val gyldigFraOgMed = LocalDate.of(2026, 11, 5)
            val manglendeVerdi: String? = null
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
                    Opplysning("Uten verdi", manglendeVerdi, null, UUID.randomUUID()),
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

        "henter dato som LocalDate og verifiserer den etter JSONB-innlesing" {
            val migrering = nyMigrering()
            val produsent = Fodselsdato()
            val dato = LocalDate.of(1980, 1, 1)
            val opplysning = Opplysning(produsent.opplysningsNavn, dato, dato, produsent.opplysningsId)
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(opplysning))

            val hentet =
                repository.hentOpplysning(migrering.migreringId, opplysning.uuid, produsent.verdiType).shouldNotBeNull()

            hentet.verdi.shouldBeInstanceOf<LocalDate>() shouldBe dato
            hentet.gyldigFraOgMed shouldBe dato
            produsent.verifiser(dato, migrering.migreringId, repository).ok shouldBe true
            produsent.verifiser(dato.plusDays(1), migrering.migreringId, repository).ok shouldBe false
        }

        "henter penger som BigDecimal uten presisjonstap" {
            val migrering = nyMigrering()
            val produsent = Dagsats()
            val belop = BigDecimal("9007199254740993.123456789")
            val opplysning = Opplysning(produsent.opplysningsNavn, belop, null, produsent.opplysningsId)
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(opplysning))

            val hentet =
                repository.hentOpplysning(migrering.migreringId, opplysning.uuid, produsent.verdiType).shouldNotBeNull()

            hentet.verdi.shouldBeInstanceOf<BigDecimal>() shouldBe belop
            produsent.verifiser(belop, migrering.migreringId, repository).ok shouldBe true
            produsent.verifiser(belop + BigDecimal.ONE, migrering.migreringId, repository).ok shouldBe false
        }

        "henter barneliste og hele inntekten som domenetyper" {
            val migrering = nyMigrering()
            val barn = BarnListe(søknadbarnId = UUID.randomUUID(), barn = emptyList())
            val inntekt =
                InntektV1(
                    inntektsId = "test-inntekt",
                    inntektsListe = emptyList(),
                    sisteAvsluttendeKalenderMåned = YearMonth.of(2026, 10),
                    hentetTidspunkt = LocalDateTime.of(2026, 11, 1, 12, 0),
                )
            val barneOpplysning = Opplysning("Barn", barn, null, UUID.randomUUID())
            val inntektsOpplysning = Opplysning("Inntekt", inntekt, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(barneOpplysning, inntektsOpplysning))

            val hentetBarn =
                repository.hentOpplysning(migrering.migreringId, barneOpplysning.uuid, BarnListe::class).shouldNotBeNull()
            val hentetInntekt =
                repository.hentOpplysning(migrering.migreringId, inntektsOpplysning.uuid, InntektV1::class).shouldNotBeNull()

            hentetBarn.verdi.shouldBeInstanceOf<BarnListe>() shouldBe barn
            hentetInntekt.verdi.shouldBeInstanceOf<InntektV1>().inntektsId shouldBe inntekt.inntektsId
            hentetInntekt.verdi?.inntektsListe shouldBe inntekt.inntektsListe
            hentetInntekt.verdi?.hentetTidspunkt shouldBe inntekt.hentetTidspunkt
        }

        "skiller mellom manglende rad og en opplysning uten verdi" {
            val migrering = nyMigrering()
            val verdi: LocalDate? = null
            val opplysning = Opplysning("Dato", verdi, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(opplysning))

            repository.hentOpplysning(migrering.migreringId, UUID.randomUUID(), LocalDate::class) shouldBe null
            repository
                .hentOpplysning(migrering.migreringId, opplysning.uuid, LocalDate::class)
                .shouldNotBeNull()
                .verdi shouldBe null
        }

        "avviser ugyldig lagret verdi uten aa eksponere den i feilmeldingen" {
            val migrering = nyMigrering()
            val feilVerdi = "ugyldig-opplysningsverdi"
            val datoverdi = Opplysning("Dato", feilVerdi, null, UUID.randomUUID())
            val inntektsverdi = Opplysning("Inntekt", feilVerdi, null, UUID.randomUUID())
            repository.lagreMigrering(migrering)
            repository.lagreOpplysninger(migrering.migreringId, listOf(datoverdi, inntektsverdi))

            listOf(datoverdi to LocalDate::class, inntektsverdi to InntektV1::class).forEach { (opplysning, verdiType) ->
                val feil =
                    shouldThrow<IllegalStateException> {
                        repository.hentOpplysning(migrering.migreringId, opplysning.uuid, verdiType)
                    }

                feil.message shouldContain opplysning.uuid.toString()
                feil.message shouldNotContain feilVerdi
                feil.cause shouldBe null
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

            val hentet =
                repository.hentOpplysningerForMigrering(migrering.migreringId).associateBy { it.opplysning.uuid }
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
            repository.lagreOpplysninger(
                migrering.migreringId,
                listOf(migrert, etterspurt, ogsåEtterspurt, ikkeEtterspurt),
            )
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
            repository.hentManglendeOpplysningerForMigrering(
                UUID.randomUUID(),
                listOf(UUID.randomUUID()),
            ) shouldBe emptyList()
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
                    migreringId = migrering.migreringId,
                    meldingId = meldingId,
                    hendelse = "MIGRERING_STARTET",
                    ident = migrering.ident,
                    jsonData = jsonData,
                    opprinnelse = opprinnelse,
                    tidspunkt = LocalDateTime.now(),
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
                    migreringId = migrering.migreringId,
                    meldingId = meldingId,
                    hendelse = "MIGRERING_FULLFØRT",
                    ident = migrering.ident,
                    jsonData = """{"antall":43}""",
                    opprinnelse = if (opprinnelse == "Intern") "Ekstern" else "Intern",
                    tidspunkt = LocalDateTime.now(),
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
                hendelse = "MIGRERING_STARTET",
                ident = migrering.ident,
                jsonData = jsonData,
                opprinnelse = "Ekstern",
                tidspunkt = LocalDateTime.now(),
            )

            repository.lagreMigreringHendelse(
                migreringId = migrering.migreringId,
                meldingId = meldingId,
                hendelse = "migrering_fullført",
                ident = migrering.ident,
                jsonData = """{"antall":43}""",
                opprinnelse = "Intern",
                tidspunkt = LocalDateTime.now(),
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

        "henter hendelser sidevis i ID-rekkefølge og bare for valgt migrering" {
            val migrering = nyMigrering()
            val annenMigrering = nyMigrering()
            repository.lagreMigrering(migrering)
            repository.lagreMigrering(annenMigrering)
            val meldingIder = List(3) { UUID.randomUUID() }
            meldingIder.forEachIndexed { index, meldingId ->
                repository.lagreMigreringHendelse(
                    migrering.migreringId,
                    meldingId,
                    hendelse = "hendelse_$index",
                    ident = migrering.ident,
                    jsonData = """{"nummer":$index}""",
                    opprinnelse = "Intern",
                    tidspunkt = LocalDateTime.now(),
                )
                repository.lagreMigreringHendelse(
                    migreringId = annenMigrering.migreringId,
                    meldingId = UUID.randomUUID(),
                    hendelse = "annen_hendelse",
                    ident = annenMigrering.ident,
                    jsonData = "{}",
                    opprinnelse = "Ekstern",
                    tidspunkt = LocalDateTime.now(),
                )
            }
            databaseSession.session { session ->
                session.run(
                    queryOf(
                        "UPDATE migrering_hendelse SET tidspunkt = NOW() + INTERVAL '1 day' WHERE melding_id = :melding_id",
                        mapOf("melding_id" to meldingIder.first()),
                    ).asUpdate,
                )
            }

            val førsteSide = repository.migreringHendelser(migrering.migreringId, lastEventId = 0, limit = 2)
            førsteSide.map { it.meldingId } shouldBe meldingIder.take(2)
            førsteSide.map { it.id } shouldBe førsteSide.map { it.id }.sorted()
            førsteSide.first().apply {
                migreringId shouldBe migrering.migreringId
                sakId shouldBe migrering.sakId
                initiertAv shouldBe migrering.initiertAv
                hendelse shouldBe "hendelse_0"
                opprinnelse shouldBe "Intern"
            }

            val sisteSide =
                repository.migreringHendelser(migrering.migreringId, lastEventId = førsteSide.last().id, limit = 2)
            sisteSide.map { it.meldingId } shouldBe meldingIder.takeLast(1)
            repository.migreringHendelser(
                migrering.migreringId,
                lastEventId = sisteSide.single().id,
                limit = 2,
            ) shouldBe emptyList()
            repository.migreringHendelser(UUID.randomUUID()) shouldBe emptyList()
        }

        "avviser migreringshendelser for en migrering som ikke finnes" {
            shouldThrow<SQLException> {
                repository.lagreMigreringHendelse(
                    migreringId = UUID.randomUUID(),
                    meldingId = UUID.randomUUID(),
                    hendelse = "migrering_startet",
                    ident = "testident",
                    jsonData = "{}",
                    opprinnelse = "Ekstern",
                    tidspunkt = LocalDateTime.now(),
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
        simulering = true,
    )
