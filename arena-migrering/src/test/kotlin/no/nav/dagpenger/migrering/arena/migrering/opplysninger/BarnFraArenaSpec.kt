package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.Barn
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.Barnekilde
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.exc.MismatchedInputException
import java.time.LocalDate
import java.util.UUID

class BarnFraArenaSpec :
    StringSpec({
        val produsent = BarnFraArena()
        val migreringId = UUID.randomUUID()
        val barn =
            Barn(
                kilde = Barnekilde.Register,
                ident = "test-ident",
                fødselsdato = LocalDate.of(2020, 1, 1),
                fornavnOgMellomnavn = "Test",
                etternavn = "Testbarn",
                statsborgerskap = "NOR",
                oppholdsland = "NOR",
                kvalifiserer = true,
                forsørgeransvar = false,
                begrunnelse = "Testbegrunnelse",
            )
        val annetBarn = barn.copy(ident = "annen-test-ident", fødselsdato = LocalDate.of(2022, 1, 1))
        val barnListe =
            BarnListe(
                søknadbarnId = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c"),
                barn = listOf(barn, annetBarn),
            )

        fun opplysning(verdi: Any?): Opplysning<Any> =
            Opplysning(
                navn = produsent.opplysningsNavn,
                verdi = verdi,
                gyldigFraOgMed = LocalDate.of(2026, 11, 1),
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

        "sammenligner databaseverdien med JSON-objektet fra simuleringsmeldingen" {
            val arenaJson = objectMapper.writeValueAsString(opplysning(barnListe))
            val arena = objectMapper.readValue(arenaJson, Opplysning::class.java)
            val dpSak =
                objectMapper.readTree(
                    objectMapper.writeValueAsString(barnListe.copy(barn = barnListe.barn.reversed())),
                )

            val resultat = verifiser(arena, dpSak)

            resultat.verifisert shouldBe true
            resultat.arenaVerdi shouldBe arena.verdi
            resultat.dpSakVerdi shouldBe dpSak
        }

        "sammenligner ferdig deserialiserte barnelister etter innhold" {
            val kopi = barnListe.copy(barn = barnListe.barn.map { it.copy() })

            verifiser(opplysning(barnListe), kopi).verifisert shouldBe true
        }

        "ignorerer rekkefolgen paa barna" {
            val omvendt = barnListe.copy(barn = barnListe.barn.reversed())

            verifiser(opplysning(barnListe), omvendt).verifisert shouldBe true
        }

        "bevarer antall forekomster av hvert barn" {
            val arena = barnListe.copy(barn = listOf(barn, barn, annetBarn))
            val dpSak = barnListe.copy(barn = listOf(barn, annetBarn, annetBarn))
            val omvendt = arena.copy(barn = arena.barn.reversed())

            verifiser(opplysning(arena), dpSak).verifisert shouldBe false
            verifiser(opplysning(arena), omvendt).verifisert shouldBe true
        }

        "sammenligner soknadbarnId" {
            verifiser(opplysning(barnListe), barnListe.copy(søknadbarnId = null)).verifisert shouldBe false
            verifiser(opplysning(barnListe), barnListe.copy(søknadbarnId = UUID.randomUUID())).verifisert shouldBe false
        }

        "sammenligner alle feltene paa hvert barn" {
            val endringer =
                listOf(
                    "kilde" to barn.copy(kilde = Barnekilde.Saksbehandler),
                    "ident" to barn.copy(ident = "endret-test-ident"),
                    "fodselsdato" to barn.copy(fødselsdato = barn.fødselsdato.plusDays(1)),
                    "fornavnOgMellomnavn" to barn.copy(fornavnOgMellomnavn = null),
                    "etternavn" to barn.copy(etternavn = null),
                    "statsborgerskap" to barn.copy(statsborgerskap = null),
                    "oppholdsland" to barn.copy(oppholdsland = null),
                    "kvalifiserer" to barn.copy(kvalifiserer = false),
                    "forsorgeransvar" to barn.copy(forsørgeransvar = true),
                    "begrunnelse" to barn.copy(begrunnelse = null),
                )

            endringer.forEach { (felt, endretBarn) ->
                withClue(felt) {
                    val endretListe = barnListe.copy(barn = listOf(endretBarn, annetBarn))
                    verifiser(opplysning(barnListe), endretListe).verifisert shouldBe false
                }
            }
        }

        "avviser ulikt antall barn" {
            verifiser(opplysning(barnListe), barnListe.copy(barn = listOf(barn))).verifisert shouldBe false
        }

        "sammenligner tomme barnelister" {
            val tom = BarnListe(barn = emptyList())

            verifiser(opplysning(tom), tom.copy()).verifisert shouldBe true
            verifiser(opplysning(barnListe), barnListe.copy(barn = emptyList())).verifisert shouldBe false
        }

        "beholder null-regelen uten aa forveksle null med en tom barneliste" {
            val tom = BarnListe(barn = emptyList())

            verifiser(opplysning(null), null).verifisert shouldBe true
            verifiser(opplysning(null), barnListe).verifisert shouldBe false
            verifiser(opplysning(barnListe), null).verifisert shouldBe false
            verifiser(opplysning(null), tom).verifisert shouldBe false
            verifiser(opplysning(tom), null).verifisert shouldBe false
            verifiser(null, null).verifisert shouldBe false
        }

        "lar ugyldig barneliste gi konverteringsfeil" {
            shouldThrow<MismatchedInputException> {
                verifiser(opplysning(barnListe), mapOf("barn" to "ikke-en-liste"))
            }
            shouldThrow<MismatchedInputException> {
                verifiser(opplysning(mapOf("barn" to "ikke-en-liste")), barnListe)
            }
        }
    })
