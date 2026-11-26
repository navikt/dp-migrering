package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.behandling.api.models.BarnVerdiDTO
import no.nav.dagpenger.behandling.api.models.BarnVerdiDTOKildeDTO
import no.nav.dagpenger.behandling.api.models.BarnelisteDTO
import no.nav.dagpenger.behandling.api.models.DataTypeDTO
import no.nav.dagpenger.behandling.api.models.TekstVerdiDTO
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

        "bevarer alle barnefeltene og soknadsbarn-ID fra behandlingsapi" {
            val dto =
                BarnelisteDTO(
                    søknadBarnId = barnListe.søknadbarnId,
                    verdi =
                        listOf(
                            BarnVerdiDTO(
                                kilde = BarnVerdiDTOKildeDTO.REGISTER,
                                ident = barn.ident,
                                fødselsdato = barn.fødselsdato,
                                fornavnOgMellomnavn = barn.fornavnOgMellomnavn,
                                etternavn = barn.etternavn,
                                statsborgerskap = barn.statsborgerskap,
                                oppholdsland = barn.oppholdsland,
                                forsørgeransvar = barn.forsørgeransvar,
                                kvalifiserer = barn.kvalifiserer,
                                begrunnelse = barn.begrunnelse,
                            ),
                        ),
                )

            produsent.verdiFraDto(dto) shouldBe barnListe.copy(barn = listOf(barn))
        }

        val kilder =
            listOf(
                BarnVerdiDTOKildeDTO.REGISTER to Barnekilde.Register,
                BarnVerdiDTOKildeDTO.SØKNAD to Barnekilde.Søknad,
                BarnVerdiDTOKildeDTO.SAKSBEHANDLER to Barnekilde.Saksbehandler,
                null to null,
            )

        kilder.forEach { (dtoKilde, forventetKilde) ->
            "bevarer barnekilde $dtoKilde fra behandlingsapi" {
                val dto =
                    BarnelisteDTO(
                        verdi =
                            listOf(
                                BarnVerdiDTO(
                                    kilde = dtoKilde,
                                    fødselsdato = barn.fødselsdato,
                                    forsørgeransvar = false,
                                    kvalifiserer = false,
                                ),
                            ),
                    )

                produsent
                    .verdiFraDto(dto)
                    .barn
                    .single()
                    .kilde shouldBe forventetKilde
            }
        }

        "bevarer tom barneliste fra behandlingsapi" {
            produsent.verdiFraDto(BarnelisteDTO(verdi = emptyList())) shouldBe BarnListe(barn = emptyList())
        }

        "avviser feil DTO-type eller datatype for barn" {
            shouldThrow<IllegalArgumentException> {
                produsent.verdiFraDto(TekstVerdiDTO("ikke en barneliste"))
            }
            shouldThrow<IllegalArgumentException> {
                produsent.verdiFraDto(BarnelisteDTO(verdi = emptyList(), datatype = DataTypeDTO.TEKST))
            }
        }

        fun opplysning(verdi: BarnListe?): Opplysning<BarnListe> =
            Opplysning(
                navn = produsent.opplysningsNavn,
                verdi = verdi,
                gyldigFraOgMed = LocalDate.of(2026, 11, 1),
                uuid = produsent.opplysningsId,
            )

        fun verifiser(
            arena: Opplysning<*>?,
            dpSak: BarnListe,
        ): OpplysningKontrollResultat {
            val repository = mockk<MigreringRepository>()
            every { repository.hentOpplysning(migreringId, produsent.opplysningsId) } returns arena
            return produsent.verifiser(dpSak, migreringId, repository)
        }

        "sammenligner databaseverdien med JSON-objektet fra simuleringsmeldingen" {
            val arenaJson = objectMapper.writeValueAsString(opplysning(barnListe))
            val arena = objectMapper.readValue(arenaJson, Opplysning::class.java)
            val dpSak =
                objectMapper.readTree(
                    objectMapper.writeValueAsString(barnListe.copy(barn = barnListe.barn.reversed())),
                )

            produsent.sammenlignVerdier(arena.verdi, dpSak) shouldBe true
        }

        "sammenligner ferdig deserialiserte barnelister etter innhold" {
            val kopi = barnListe.copy(barn = barnListe.barn.map { it.copy() })

            verifiser(opplysning(barnListe), kopi).ok shouldBe true
        }

        "ignorerer rekkefolgen paa barna" {
            val omvendt = barnListe.copy(barn = barnListe.barn.reversed())

            verifiser(opplysning(barnListe), omvendt).ok shouldBe true
        }

        "bevarer antall forekomster av hvert barn" {
            val arena = barnListe.copy(barn = listOf(barn, barn, annetBarn))
            val dpSak = barnListe.copy(barn = listOf(barn, annetBarn, annetBarn))
            val omvendt = arena.copy(barn = arena.barn.reversed())

            verifiser(opplysning(arena), dpSak).ok shouldBe false
            verifiser(opplysning(arena), omvendt).ok shouldBe true
        }

        "sammenligner soknadbarnId" {
            verifiser(opplysning(barnListe), barnListe.copy(søknadbarnId = null)).ok shouldBe false
            verifiser(opplysning(barnListe), barnListe.copy(søknadbarnId = UUID.randomUUID())).ok shouldBe false
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
                    verifiser(opplysning(barnListe), endretListe).ok shouldBe false
                }
            }
        }

        "avviser ulikt antall barn" {
            verifiser(opplysning(barnListe), barnListe.copy(barn = listOf(barn))).ok shouldBe false
        }

        "sammenligner tomme barnelister" {
            val tom = BarnListe(barn = emptyList())

            verifiser(opplysning(tom), tom.copy()).ok shouldBe true
            verifiser(opplysning(barnListe), barnListe.copy(barn = emptyList())).ok shouldBe false
        }

        "beholder null-regelen uten aa forveksle null med en tom barneliste" {
            val tom = BarnListe(barn = emptyList())

            produsent.sammenlignVerdier(null, null) shouldBe true
            verifiser(opplysning(null), barnListe).ok shouldBe false
            produsent.sammenlignVerdier(barnListe, null) shouldBe false
            verifiser(opplysning(null), tom).ok shouldBe false
            produsent.sammenlignVerdier(tom, null) shouldBe false
            verifiser(null, barnListe).ok shouldBe false
        }

        "lar ugyldig barneliste gi konverteringsfeil" {
            shouldThrow<MismatchedInputException> {
                produsent.sammenlignVerdier(barnListe, mapOf("barn" to "ikke-en-liste"))
            }
            shouldThrow<MismatchedInputException> {
                produsent.sammenlignVerdier(mapOf("barn" to "ikke-en-liste"), barnListe)
            }
        }
    })
