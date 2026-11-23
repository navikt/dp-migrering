package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.inntekt.v1.InntektKlasse
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.harSammeInnholdSom
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.tilInntekt
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.exc.MismatchedInputException
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

class InntektsopplysningerSpec :
    StringSpec({
        val produsent = Inntektsopplysninger()
        val migreringId = UUID.randomUUID()
        val sisteMaaned = YearMonth.of(2026, 5)
        val maaneder =
            (0L..1L).map {
                KlassifisertInntektMåned(
                    årMåned = sisteMaaned.minusMonths(it),
                    klassifiserteInntekter =
                        listOf(KlassifisertInntekt(1000.toBigDecimal(), InntektKlasse.ARBEIDSINNTEKT)),
                )
            }

        fun inntekt(
            id: String = "test-inntekt",
            liste: List<KlassifisertInntektMåned> = maaneder,
            redigert: Boolean? = false,
            begrunnelse: String? = null,
            siste: YearMonth = sisteMaaned,
            hentet: LocalDateTime? = LocalDateTime.of(2026, 6, 1, 12, 0),
        ) = InntektV1(id, liste, redigert, begrunnelse, siste, hentet)

        fun opplysning(verdi: Any?): Opplysning<Any> =
            Opplysning(
                navn = produsent.opplysningsNavn,
                verdi = verdi,
                gyldigFraOgMed = null,
                uuid = produsent.opplysningsId,
            )

        fun verifiser(
            arena: Opplysning<*>?,
            dpSak: Any?,
        ): OpplysningVerifisertResultat {
            val repository = mockk<MigreringRepository>()
            every { repository.hentOpplysning(migreringId, produsent.opplysningsId) } returns arena
            return produsent.verifiser(opplysning(dpSak), migreringId, repository)
        }

        "sammenligner databaseverdien med inntektsobjektet fra simuleringsmeldingen" {
            val json = objectMapper.writeValueAsString(opplysning(inntekt()))
            val arena = objectMapper.readValue(json, Opplysning::class.java)
            val dpSak = objectMapper.readTree(json).path("verdi")

            val resultat = verifiser(arena, dpSak)

            resultat.verifisert shouldBe true
            resultat.arenaVerdi shouldBe arena.verdi
            resultat.dpSakVerdi shouldBe dpSak
        }

        "sammenligner ogsaa ferdig deserialiserte inntekter" {
            verifiser(opplysning(inntekt()), inntekt()).verifisert shouldBe true
        }

        "konverterer databaseverdien naar dp-sak allerede er deserialisert" {
            val json = objectMapper.writeValueAsString(opplysning(inntekt()))
            val arena = objectMapper.readValue(json, Opplysning::class.java)

            verifiser(arena, inntekt()).verifisert shouldBe true
        }

        "konverterer Jackson-objektet naar Arena allerede er deserialisert" {
            val dpSak = objectMapper.readTree(objectMapper.writeValueAsString(inntekt()))

            verifiser(opplysning(inntekt()), dpSak).verifisert shouldBe true
        }

        "behandler historiske verdityper som manglende inntekt" {
            val historiskeVerdier =
                listOf(
                    42,
                    42L,
                    42.5,
                    42.toBigDecimal(),
                    "historisk verdi",
                    true,
                    listOf(42),
                    objectMapper.readTree("42"),
                    objectMapper.readTree("[42]"),
                )
            val bruttoArbeidsinntekt = BruttoArbeidsinntekt()

            historiskeVerdier.forEach { historiskVerdi ->
                historiskVerdi.tilInntekt() shouldBe null
                verifiser(opplysning(historiskVerdi), inntekt()).verifisert shouldBe false
                verifiser(opplysning(inntekt()), historiskVerdi).verifisert shouldBe false
                verifiser(opplysning(historiskVerdi), historiskVerdi).verifisert shouldBe false
                bruttoArbeidsinntekt.sammenlignVerdier(historiskVerdi, inntekt()) shouldBe false
                bruttoArbeidsinntekt.sammenlignVerdier(inntekt(), historiskVerdi) shouldBe false
            }
        }

        "lar ugyldige inntektsobjekter gi konverteringsfeil paa begge sider" {
            val ugyldigMap =
                mapOf(
                    "inntektsId" to "test-inntekt",
                    "inntektsListe" to "ikke-en-liste",
                    "sisteAvsluttendeKalenderMåned" to "2026-05",
                )
            val ugyldigeObjekter =
                listOf(
                    ugyldigMap,
                    objectMapper.readTree(objectMapper.writeValueAsString(ugyldigMap)),
                )

            ugyldigeObjekter.forEach { ugyldigObjekt ->
                shouldThrow<MismatchedInputException> {
                    verifiser(opplysning(ugyldigObjekt), inntekt())
                }
                shouldThrow<MismatchedInputException> {
                    verifiser(opplysning(inntekt()), ugyldigObjekt)
                }
            }
        }

        "ignorerer rekkefolgen paa maanedene" {
            verifiser(opplysning(inntekt()), inntekt(liste = maaneder.reversed())).verifisert shouldBe true
        }

        "bevarer antall forekomster av hver maaned" {
            val arena = inntekt(liste = listOf(maaneder[0], maaneder[0], maaneder[1]))
            val dpSak = inntekt(liste = listOf(maaneder[0], maaneder[1], maaneder[1]))

            verifiser(opplysning(arena), dpSak).verifisert shouldBe false
        }

        "oppdager endringer i alle feltene" {
            val endringer =
                listOf(
                    inntekt(id = "annen-inntekt"),
                    inntekt(liste = emptyList()),
                    inntekt(redigert = true),
                    inntekt(redigert = null),
                    inntekt(begrunnelse = "Endret"),
                    inntekt(siste = sisteMaaned.minusMonths(1)),
                    inntekt(hentet = null),
                )

            endringer.forEach {
                verifiser(opplysning(inntekt()), it).verifisert shouldBe false
            }
        }

        "verifiserer ikke naar inntekter mangler" {
            verifiser(null, inntekt()).verifisert shouldBe false
            verifiser(opplysning(null), inntekt()).verifisert shouldBe false
            verifiser(opplysning(inntekt()), null).verifisert shouldBe false
            verifiser(opplysning(null), null).verifisert shouldBe false
        }

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
