package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.migrering.arena.migrering.ArenaSakMigrering
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.objectMapper
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class OpplysningProdusentSpec :
    StringSpec({
        val produsent = Dagsats()
        val migreringId = UUID.randomUUID()

        fun opplysning(
            verdi: BigDecimal?,
            uuid: UUID = produsent.opplysningsId,
        ): Opplysning<BigDecimal> =
            Opplysning(
                navn = produsent.opplysningsNavn,
                verdi = verdi,
                gyldigFraOgMed = LocalDate.of(2026, 11, 1),
                uuid = uuid,
            )

        fun verifiser(
            arena: Opplysning<BigDecimal>?,
            dpSak: BigDecimal,
        ): OpplysningKontrollResultat {
            val repository = mockk<MigreringRepository>()
            every { repository.hentOpplysning(migreringId, produsent.opplysningsId, produsent.verdiType) } returns arena
            return produsent.kontroller(dpSak, migreringId, repository)
        }

        listOf(true, false, "tekst", 123, 123L, 123.5, 1.toBigDecimal()).forEach { verdi ->
            "verifiserer like verdier av typen ${verdi.javaClass.simpleName}: $verdi" {
                produsent.sammenlignVerdier(verdi, verdi) shouldBe true
            }
        }

        "sammenligner pengetall uten aa kreve lik skala" {
            produsent.sammenlignVerdier(BigDecimal("1.00"), BigDecimal("1.0")) shouldBe true
            produsent.sammenlignVerdier(BigDecimal("0"), BigDecimal("0.000")) shouldBe true
            produsent.sammenlignVerdier(BigDecimal("1.00"), BigDecimal("1.01")) shouldBe false
            produsent.sammenlignVerdier(BigDecimal.ONE, 1) shouldBe false
        }

        "sammenligner LocalDate uten aa konvertere tekstverdier" {
            val dato = LocalDate.of(2026, 11, 26)

            produsent.sammenlignVerdier(dato, LocalDate.of(2026, 11, 26)) shouldBe true
            produsent.sammenlignVerdier(dato, dato.plusDays(1)) shouldBe false
            produsent.sammenlignVerdier(dato, dato.toString()) shouldBe false
        }

        listOf(
            true to false,
            "en" to "to",
            1 to 2,
            1L to 2L,
            1.0 to 2.0,
            1 to 1L,
            1 to 1.0,
            "1" to 1,
        ).forEach { (arena, dpSak) ->
            "avviser ulik verdi eller type: $arena (${arena.javaClass.simpleName}) og $dpSak (${dpSak.javaClass.simpleName})" {
                produsent.sammenlignVerdier(arena, dpSak) shouldBe false
            }
        }

        "beholder null-regelen uten aa godta manglende databaserad" {
            produsent.sammenlignVerdier(null, null) shouldBe true
            verifiser(opplysning(null), BigDecimal.ONE).ok shouldBe false
            produsent.sammenlignVerdier(1, null) shouldBe false
            verifiser(null, BigDecimal.ONE).ok shouldBe false
        }

        "verifiserer en typet opplysning" {
            val resultat = verifiser(opplysning(123.toBigDecimal()), 123.toBigDecimal())

            resultat.ok shouldBe true
            resultat.uuid shouldBe produsent.opplysningsId
            resultat.navn shouldBe produsent.opplysningsNavn
            resultat.arenaVerdi shouldBe 123.toBigDecimal()
            resultat.dpSakVerdi shouldBe 123.toBigDecimal()
        }

        "avviser ulik UUID selv om verdiene er like" {
            verifiser(opplysning(BigDecimal.ONE, UUID.randomUUID()), BigDecimal.ONE).ok shouldBe false
        }

        val dato = LocalDate.of(2026, 11, 1)

        fun kontekst(
            kode: String? = null,
            verdi: String = "9007199254740993.123456789",
        ): MigreringsKontekst {
            val sak = mockk<ArenaSakMigrering>()
            every { sak.vedtakfakta } returns
                listOfNotNull(kode?.let { ArenaSakMigrering.ArenaVedtakfaktaMigrering(it, verdi, dato) })
            every { sak.grunnbelop } returns
                listOfNotNull(kode?.let { ArenaSakMigrering.ArenaSatsMigrering(it, verdi, dato) })
            every { sak.gjenstaendeTeller } returns
                listOfNotNull(
                    kode?.let { ArenaSakMigrering.ArenaGjenstaendeTellerMigrering("GJENNTELLER", it, verdi, dato) },
                )
            return MigreringsKontekst(sak, "testbruker", opprettetTidspunkt = dato.atStartOfDay())
        }

        val pengeprodusenterFraArena =
            listOf(
                Dagpengegrunnlag() to "GRUNN",
                Dagsats() to "DAGS",
                DagsatsMedBarnetilleggEtterSamordningOg90Regel() to "DAGS",
                Egenandel() to "EGENANDDP",
                Grunnbelop() to "GRUNNBELOP",
                InntektPeriode1() to "INTSISTE",
                InntektPeriode2() to "INTNESTS",
                InntektPeriode3() to "INTTREDS",
                SamordnetDagsatsUtenBarnetillegg() to "DAGS",
                UkessatsEtterSamordning() to "UKES",
                UkessatsForSamordning() to "UKESFSAM",
                GjenstaendeEgenandel() to "EGENA",
            )

        pengeprodusenterFraArena.forEach { (pengeprodusent, kode) ->
            "bevarer alle desimaler fra Arena for ${pengeprodusent::class.simpleName}" {
                val resultat = pengeprodusent.produser(kontekst(kode))

                resultat.verdi.shouldBeInstanceOf<BigDecimal>() shouldBe BigDecimal("9007199254740993.123456789")
                resultat.gyldigFraOgMed shouldBe dato
                resultat.uuid shouldBe pengeprodusent.opplysningsId
            }

            "bevarer manglende verdi for ${pengeprodusent::class.simpleName}" {
                pengeprodusent.produser(kontekst()).verdi shouldBe
                    if (pengeprodusent is GjenstaendeEgenandel) BigDecimal.ZERO else null
            }
        }

        val fastePengeverdier =
            listOf(
                EtterlonnBelop() to 0,
                ForbruktEgenandel() to 25000,
                ForeldrepengerDagsats() to 1000,
                GarantilottFraGarantikassenForFiskereBelop() to 0,
                OmsorgspengerDagsats() to 0,
                OppleringspengerDagsats() to 0,
                PleiepengerDagsats() to 0,
                SvangerskapspengerDagsats() to 0,
                SykepengerDagsats() to 0,
                UforeDagsats() to 0,
                UforepensjonFraOffentligPensjonsordningBelop() to 0,
                VartpengerBelop() to 0,
                VentelonnBelop() to 0,
            )

        fastePengeverdier.forEach { (pengeprodusent, forventet) ->
            "bevarer fast beløp som BigDecimal for ${pengeprodusent::class.simpleName}" {
                pengeprodusent.produser(kontekst()).verdi.shouldBeInstanceOf<BigDecimal>() shouldBe forventet.toBigDecimal()
            }
        }

        "bruker manglende pengeverdi for grunnbeløp for grunnlag" {
            val grunnbelop = GrunnbelopForGrunnlag()

            grunnbelop.produser(kontekst()).verdi shouldBe null
            grunnbelop.status shouldBe OpplysningProdusent.Status.Dummy
        }

        "bruker desimaltall for minimum vanlig arbeidstid" {
            MinimumVanligArbeidstid().produser(kontekst()).verdi.shouldBeInstanceOf<Double>() shouldBe 0.0
        }

        val komplekseVerdier =
            listOf(
                mapOf("felt" to "testverdi"),
                listOf("testverdi"),
                setOf("testverdi"),
                arrayOf("testverdi"),
                intArrayOf(1),
                objectMapper.readTree("""{"felt":"testverdi"}"""),
                objectMapper.readTree("""["testverdi"]"""),
                1.toBigInteger(),
                1.0f,
                UUID.randomUUID(),
                Any(),
            )

        komplekseVerdier.forEach { verdi ->
            "krever egen sammenligning for ${verdi.javaClass.name} paa begge sider" {
                listOf(
                    verdi to 1,
                    1 to verdi,
                    verdi to verdi,
                    null to verdi,
                    verdi to null,
                ).forEach { (arena, dpSak) ->
                    val feil =
                        shouldThrow<UnsupportedOperationException> {
                            produsent.sammenlignVerdier(arena, dpSak)
                        }

                    feil.message shouldContain produsent.opplysningsNavn
                    feil.message shouldContain produsent.opplysningsId.toString()
                    feil.message shouldContain verdi.javaClass.name
                    feil.message shouldContain "Overstyr sammenlignVerdier"
                    feil.message shouldNotContain "testverdi"
                }
            }
        }

        "avviser ukjent datatype selv om databaseraden mangler" {
            shouldThrow<UnsupportedOperationException> {
                produsent.sammenlignVerdier(null, emptyList<String>())
            }
        }

        "bevarer verditype og metadata i fabrikkmetodene" {
            val dato = LocalDate.of(2026, 11, 1)
            val uuid = UUID.randomUUID()
            val opplysninger =
                listOf(
                    Opplysning("Desimaltall", 1.toBigDecimal(), dato, uuid),
                    Opplysning("Tekst", "tekst", dato, uuid),
                    Opplysning("Dato", dato, dato, uuid),
                    Opplysning("Boolsk", true, dato, uuid),
                    Opplysning("Heltall", 1, dato, uuid),
                    Opplysning("Flyttall", 1.5, dato, uuid),
                    Opplysning("Barn", BarnListe(barn = emptyList()), dato, uuid),
                )
            val verdier = listOf(1.toBigDecimal(), "tekst", dato, true, 1, 1.5, BarnListe(barn = emptyList()))

            opplysninger.zip(verdier).forEach { (opplysning, verdi) ->
                opplysning.verdi shouldBe verdi
                opplysning.verdi?.javaClass shouldBe verdi.javaClass
                opplysning.gyldigFraOgMed shouldBe dato
                opplysning.uuid shouldBe uuid
            }
        }
    })
