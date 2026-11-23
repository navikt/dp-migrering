package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.harSammeInnholdSom
import no.nav.dagpenger.migrering.objectMapper

class InntektsopplysningerSpec :
    StringSpec({

        "skal kunne sammenligne inntekter" {
            val inntekt1 = objectMapper.readValue(inntekt1, InntektV1::class.java)
            val inntekt2 = objectMapper.readValue(inntekt2, InntektV1::class.java)

            inntekt1.harSammeInnholdSom(inntekt2) shouldBe true
        }
    })

// language=json
private val inntekt1 =
    """
    {
       "inntektsId": "01M523KEMZJNQ6T9Q8AX3M6FDC",
       "inntektsListe": [
         {
           "årMåned": "2025-07",
           "klassifiserteInntekter": [
             {
               "beløp": 480000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         },
         {
           "årMåned": "2024-07",
           "klassifiserteInntekter": [
             {
               "beløp": 360000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         },
         {
           "årMåned": "2026-07",
           "klassifiserteInntekter": [
             {
               "beløp": 480000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         }
       ],
       "hentetTidspunkt": "2026-09-01T12:19:48",
       "manueltRedigert": false,
       "sisteAvsluttendeKalenderMåned": "2026-07"
     }
    """.trimIndent()

// language=json
private val inntekt2 =
    """
    {
       "inntektsId": "01M523KEMZJNQ6T9Q8AX3M6FDC",
       "inntektsListe": [
         {
           "harAvvik": null,
           "årMåned": "2024-07",
           "klassifiserteInntekter": [
             {
               "beløp": 360000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         },
         {
           "harAvvik": null,
           "årMåned": "2025-07",
           "klassifiserteInntekter": [
             {
               "beløp": 480000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         },
         {
           "harAvvik": null,
           "årMåned": "2026-07",
           "klassifiserteInntekter": [
             {
               "beløp": 480000,
               "inntektKlasse": "DAGPENGER"
             }
           ]
         }
       ],
       "manueltRedigert": false,
       "sisteAvsluttendeKalenderMåned": "2026-07"
     }
    """.trimIndent()
