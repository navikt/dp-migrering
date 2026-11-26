package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.behandling.api.models.DataTypeDTO
import no.nav.dagpenger.behandling.api.models.HeltallVerdiDTO
import no.nav.dagpenger.behandling.api.models.TekstVerdiDTO
import no.nav.dagpenger.migrering.arena.migrering.ArenaSakMigrering
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.harSammeInnholdSom
import no.nav.dagpenger.migrering.objectMapper
import java.util.UUID

class InntektsopplysningerSpec :
    StringSpec({
        val inntekt = objectMapper.readValue(inntekt1, InntektV1::class.java)
        val migreringId = UUID.randomUUID()

        fun kontekst(verdi: InntektV1?): MigreringsKontekst {
            val sak = mockk<ArenaSakMigrering>()
            every { sak.inntekt } returns verdi
            return MigreringsKontekst(sakTilMigrering = sak, initiertAv = "testbruker")
        }

        fun <T : Any> repositoryFor(
            produsent: OpplysningProdusent<T>,
            opplysning: Opplysning<T>?,
        ): MigreringRepository {
            val repository = mockk<MigreringRepository>()
            every {
                repository.hentOpplysning(migreringId, produsent.opplysningsId, produsent.verdiType)
            } returns opplysning
            return repository
        }

        listOf(Inntektsopplysninger(), BruttoArbeidsinntekt()).forEach { produsent ->
            "produserer hele inntekten for ${produsent::class.simpleName}" {
                val opplysning = produsent.produser(kontekst(inntekt))

                opplysning.verdi.shouldBeInstanceOf<InntektV1>() shouldBe inntekt
                opplysning.gyldigFraOgMed shouldBe inntekt.hentetTidspunkt?.toLocalDate()
                opplysning.uuid shouldBe produsent.opplysningsId
            }

            "bevarer manglende inntekt for ${produsent::class.simpleName}" {
                produsent.produser(kontekst(null)).verdi shouldBe null
            }

            "leser inntektsreferanse fra tekst- og inntekts-DTO for ${produsent::class.simpleName}" {
                val repository = repositoryFor(produsent, produsent.produser(kontekst(inntekt)))

                listOf(DataTypeDTO.TEKST, DataTypeDTO.INNTEKT).forEach { datatype ->
                    val resultat =
                        produsent.verifiserDto(
                            TekstVerdiDTO(inntekt.inntektsId, datatype),
                            migreringId,
                            repository,
                        )

                    resultat.ok shouldBe true
                    resultat.arenaVerdi shouldBe inntekt.inntektsId
                    resultat.dpSakVerdi shouldBe inntekt.inntektsId
                }
            }

            "avviser feil datatype for ${produsent::class.simpleName}" {
                val repository = mockk<MigreringRepository>()
                shouldThrow<IllegalArgumentException> {
                    produsent.verifiserDto(HeltallVerdiDTO(1), migreringId, repository)
                }
                shouldThrow<IllegalArgumentException> {
                    produsent.verifiserDto(TekstVerdiDTO("referanse", DataTypeDTO.ULID), migreringId, repository)
                }
            }

            "verifiserer kun inntekts-ID for ${produsent::class.simpleName}" {
                val repository = repositoryFor(produsent, produsent.produser(kontekst(inntekt)))

                produsent
                    .verifiserDto(TekstVerdiDTO(inntekt.inntektsId), migreringId, repository)
                    .ok shouldBe true
                produsent
                    .verifiserDto(TekstVerdiDTO("annen-inntekts-ID"), migreringId, repository)
                    .ok shouldBe false

                every {
                    repository.hentOpplysning(migreringId, produsent.opplysningsId, produsent.verdiType)
                } returns null
                produsent
                    .verifiserDto(TekstVerdiDTO(inntekt.inntektsId), migreringId, repository)
                    .ok shouldBe false
            }

            "bevarer innholdssammenligning utenom DTO for ${produsent::class.simpleName}" {
                val endretInntekt =
                    objectMapper.readValue(inntekt1.replace("480000", "480001"), InntektV1::class.java)
                val repository = repositoryFor(produsent, produsent.produser(kontekst(inntekt)))

                produsent.verifiser(endretInntekt, migreringId, repository).ok shouldBe false
                produsent
                    .verifiserDto(TekstVerdiDTO(endretInntekt.inntektsId), migreringId, repository)
                    .ok shouldBe true
            }

            "verifiserer lagret JSON-inntekt mot ID for ${produsent::class.simpleName}" {
                val opplysning = produsent.produser(kontekst(inntekt))
                val opplysningType =
                    objectMapper.typeFactory.constructParametricType(
                        Opplysning::class.java,
                        produsent.verdiType.javaObjectType,
                    )
                val lagretOpplysning =
                    objectMapper
                        .readerFor(opplysningType)
                        .readValue<Opplysning<InntektV1>>(objectMapper.writeValueAsString(opplysning))
                val repository = repositoryFor(produsent, lagretOpplysning)

                produsent
                    .verifiserDto(TekstVerdiDTO(inntekt.inntektsId), migreringId, repository)
                    .ok shouldBe true
            }

            "avviser manglende inntektsverdi for ${produsent::class.simpleName}" {
                val repository = repositoryFor(produsent, produsent.produser(kontekst(null)))

                produsent
                    .verifiserDto(TekstVerdiDTO(inntekt.inntektsId), migreringId, repository)
                    .ok shouldBe false
            }

            "avviser feil opplysnings-ID for ${produsent::class.simpleName}" {
                val feilOpplysning =
                    Opplysning(
                        navn = produsent.opplysningsNavn,
                        verdi = inntekt,
                        gyldigFraOgMed = null,
                        uuid = UUID.randomUUID(),
                    )
                val repository = repositoryFor(produsent, feilOpplysning)

                produsent
                    .verifiserDto(TekstVerdiDTO(inntekt.inntektsId), migreringId, repository)
                    .ok shouldBe false
            }
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
