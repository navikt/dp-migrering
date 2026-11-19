package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringSimuleringResultatMelding
import no.nav.dagpenger.migrering.objectMapper
import java.time.LocalDate
import java.util.UUID

class OpplysningProdusentSpec :
    StringSpec({
        val produsent = Dagsats()
        val migreringId = UUID.randomUUID()

        fun opplysning(
            verdi: Any?,
            uuid: UUID = produsent.opplysningsId,
        ): Opplysning<Any> =
            Opplysning(
                navn = produsent.opplysningsNavn,
                verdi = verdi,
                gyldigFraOgMed = LocalDate.of(2026, 11, 1),
                uuid = uuid,
            )

        fun verifiser(
            arena: Opplysning<*>?,
            dpSak: Any?,
        ): OpplysningVerifisertResultat {
            val repository = mockk<MigreringRepository>()
            every { repository.hentOpplysning(migreringId, produsent.opplysningsId) } returns arena
            return produsent.verifiser(opplysning(dpSak), migreringId, repository)
        }

        listOf(true, false, "tekst", 123, 123L, 123.5).forEach { verdi ->
            "verifiserer like verdier av typen ${verdi.javaClass.simpleName}: $verdi" {
                val resultat = verifiser(opplysning(verdi), verdi)

                resultat.verifisert shouldBe true
                resultat.uuid shouldBe produsent.opplysningsId
                resultat.navn shouldBe produsent.opplysningsNavn
                resultat.arenaVerdi shouldBe verdi
                resultat.dpSakVerdi shouldBe verdi
            }
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
                verifiser(opplysning(arena), dpSak).verifisert shouldBe false
            }
        }

        "beholder null-regelen uten aa godta manglende databaserad" {
            verifiser(opplysning(null), null).verifisert shouldBe true
            verifiser(opplysning(null), 1).verifisert shouldBe false
            verifiser(opplysning(1), null).verifisert shouldBe false
            verifiser(null, null).verifisert shouldBe false
            verifiser(null, 1).verifisert shouldBe false
        }

        "avviser ulik UUID selv om verdiene er like" {
            verifiser(opplysning(1, UUID.randomUUID()), 1).verifisert shouldBe false
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
                LocalDate.of(2026, 11, 1),
                1.toBigDecimal(),
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
                            verifiser(opplysning(arena), dpSak)
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
                verifiser(null, emptyList<String>())
            }
        }

        "bevarer JSON-arrays fra meldingen slik at de ikke forveksles med null" {
            val packet =
                JsonMessage.newMessage(
                    MigreringSimuleringResultatMelding.EVENT_NAME,
                    mapOf(
                        "ident" to "testident",
                        "migreringId" to migreringId.toString(),
                        "simulerteOpplysninger" to listOf(opplysning(listOf("testverdi"))),
                        "sakId" to 123,
                        "migreringTidspunkt" to "2026-11-01",
                        "simulering" to true,
                        "initiertAv" to "testbruker",
                    ),
                )
            val verdi = MigreringSimuleringResultatMelding(packet).simulerteOpplysninger.single().verdi

            shouldThrow<UnsupportedOperationException> {
                verifiser(opplysning(null), verdi)
            }
        }

        "bevarer null fra simuleringsmeldingen" {
            val packet =
                JsonMessage.newMessage(
                    MigreringSimuleringResultatMelding.EVENT_NAME,
                    mapOf(
                        "ident" to "testident",
                        "migreringId" to migreringId.toString(),
                        "simulerteOpplysninger" to listOf(opplysning(null)),
                        "sakId" to 123,
                        "migreringTidspunkt" to "2026-11-01",
                        "simulering" to true,
                        "initiertAv" to "testbruker",
                    ),
                )
            val verdi = MigreringSimuleringResultatMelding(packet).simulerteOpplysninger.single().verdi

            verifiser(opplysning(null), verdi).verifisert shouldBe true
        }
    })
