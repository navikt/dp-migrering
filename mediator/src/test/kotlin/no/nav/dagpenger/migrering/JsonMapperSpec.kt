package no.nav.dagpenger.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.inntekt.v1.InntektKlasse
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

class JsonMapperSpec :
    StringSpec({
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
            val verdi = json.path("verdi")
            val inntektsListe = verdi.path("inntektsListe").asSequence().toList()

            inntektsListe.map { it.path("årMåned").asString() } shouldBe maaneder.map(YearMonth::toString)
            val gjenopprettet = objectMapper.treeToValue(verdi, Inntekt::class.java)
            gjenopprettet.inntektsListe shouldBe inntekt.inntektsListe
            gjenopprettet.sisteAvsluttendeKalenderMåned shouldBe inntekt.sisteAvsluttendeKalenderMåned
            gjenopprettet.hentetTidspunkt shouldBe inntekt.hentetTidspunkt
            verdi.has("begrunnelseManueltRedigert") shouldBe false
            inntektsListe.forEach { it.has("harAvvik") shouldBe false }
        }
    })
