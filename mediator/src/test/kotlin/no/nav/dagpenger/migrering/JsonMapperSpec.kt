package no.nav.dagpenger.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.inntekt.v1.InntektKlasse
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID
import kotlin.reflect.KClass

class JsonMapperSpec :
    StringSpec({
        fun <T : Any> lesOpplysning(
            opplysning: Opplysning<T>,
            verdiType: KClass<T>,
        ): Opplysning<T> {
            val opplysningType =
                objectMapper.typeFactory.constructParametricType(
                    Opplysning::class.java,
                    verdiType.javaObjectType,
                )
            return objectMapper.readerFor(opplysningType).readValue(objectMapper.writeValueAsString(opplysning))
        }

        "bevarer skalare verdityper ved typet JSON-innlesing" {
            val dato = LocalDate.of(2026, 11, 26)
            val uuid = UUID.randomUUID()
            val belop = BigDecimal("9007199254740993.123456789")
            val verdier =
                listOf(
                    lesOpplysning(Opplysning("Dato", dato, dato, uuid), LocalDate::class).verdi to dato,
                    lesOpplysning(Opplysning("Penger", belop, dato, uuid), BigDecimal::class).verdi to belop,
                    lesOpplysning(Opplysning("Heltall", 42, dato, uuid), Int::class).verdi to 42,
                    lesOpplysning(Opplysning("Boolsk", false, dato, uuid), Boolean::class).verdi to false,
                    lesOpplysning(Opplysning("Desimaltall", 18.75, dato, uuid), Double::class).verdi to 18.75,
                    lesOpplysning(Opplysning("Tekst", "tekst", dato, uuid), String::class).verdi to "tekst",
                )

            verdier.forEach { (innlest, forventet) ->
                innlest shouldBe forventet
                innlest?.javaClass shouldBe forventet.javaClass
            }
        }

        "bevarer null og metadata ved typet JSON-innlesing" {
            val verdi: LocalDate? = null
            val dato = LocalDate.of(2026, 11, 26)
            val uuid = UUID.randomUUID()
            val innlest = lesOpplysning(Opplysning("Dato", verdi, dato, uuid), LocalDate::class)

            innlest.verdi shouldBe null
            innlest.navn shouldBe "Dato"
            innlest.gyldigFraOgMed shouldBe dato
            innlest.uuid shouldBe uuid
        }

        "bevarer barneliste ved typet JSON-innlesing" {
            val verdi = BarnListe(søknadbarnId = UUID.randomUUID(), barn = emptyList())
            val opplysning = Opplysning("Barn", verdi, null, UUID.randomUUID())

            lesOpplysning(opplysning, BarnListe::class).verdi shouldBe verdi
        }

        "bevarer årMåned ved serialisering av inntektsopplysninger" {
            val maaneder = listOf("2024-05", "2025-05", "2026-05").map(YearMonth::parse)
            val inntekt =
                Inntekt(
                    inntektsId = "test-inntekt",
                    inntektsListe =
                        maaneder.map { maaned ->
                            KlassifisertInntektMåned(
                                årMåned = maaned,
                                klassifiserteInntekter =
                                    listOf(KlassifisertInntekt(499995.toBigDecimal(), InntektKlasse.DAGPENGER)),
                            )
                        },
                    sisteAvsluttendeKalenderMåned = maaneder.last(),
                    hentetTidspunkt = LocalDateTime.of(2026, 7, 6, 12, 24, 1),
                )
            val innlest = objectMapper.readValue(objectMapper.writeValueAsString(inntekt), Inntekt::class.java)
            val opplysning =
                Opplysning(
                    navn = "Inntektsopplysninger",
                    verdi = innlest,
                    gyldigFraOgMed = innlest.hentetTidspunkt?.toLocalDate(),
                    uuid = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04244"),
                )

            val json = objectMapper.readTree(objectMapper.writeValueAsString(opplysning))
            val typetOpplysning = lesOpplysning(opplysning, Inntekt::class)
            typetOpplysning.verdi?.inntektsId shouldBe inntekt.inntektsId
            typetOpplysning.verdi?.inntektsListe shouldBe inntekt.inntektsListe
            typetOpplysning.verdi?.sisteAvsluttendeKalenderMåned shouldBe inntekt.sisteAvsluttendeKalenderMåned
            val verdi = json.path("verdi")
            val inntektsListe = verdi.path("inntektsListe").toList()

            inntektsListe.map { it.path("årMåned").asString() } shouldBe maaneder.map(YearMonth::toString)
            val gjenopprettet = objectMapper.treeToValue(verdi, Inntekt::class.java)
            gjenopprettet.inntektsListe shouldBe inntekt.inntektsListe
            gjenopprettet.sisteAvsluttendeKalenderMåned shouldBe inntekt.sisteAvsluttendeKalenderMåned
            gjenopprettet.hentetTidspunkt shouldBe inntekt.hentetTidspunkt
            verdi.has("begrunnelseManueltRedigert") shouldBe false
            inntektsListe.forEach { it.has("harAvvik") shouldBe false }
        }
    })
