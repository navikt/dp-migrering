package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.inntekt.v1.InntektKlasse
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.objectMapper
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
        ) = Inntekt(id, liste, redigert, begrunnelse, siste, hentet)

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
    })
