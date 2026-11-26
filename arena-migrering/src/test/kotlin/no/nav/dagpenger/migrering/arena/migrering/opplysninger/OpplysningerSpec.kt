package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.prometheus.metrics.model.registry.PrometheusRegistry
import io.prometheus.metrics.model.snapshots.CounterSnapshot
import no.nav.dagpenger.behandling.api.models.BoolskVerdiDTO
import no.nav.dagpenger.behandling.api.models.DataTypeDTO
import no.nav.dagpenger.behandling.api.models.DatoVerdiDTO
import no.nav.dagpenger.behandling.api.models.DesimaltallVerdiDTO
import no.nav.dagpenger.behandling.api.models.HeltallVerdiDTO
import no.nav.dagpenger.behandling.api.models.OpplysningsverdiDTO
import no.nav.dagpenger.behandling.api.models.PengeVerdiDTO
import no.nav.dagpenger.behandling.api.models.TekstVerdiDTO
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.declaredMemberFunctions

class OpplysningerSpec :
    StringSpec({
        fun antallManglendeOpplysninger(produsent: OpplysningProdusent<*>): Double =
            PrometheusRegistry.defaultRegistry
                .scrape()
                .filterIsInstance<CounterSnapshot>()
                .singleOrNull { it.metadata.name == "arena_migrering_manglende_opplysninger" }
                ?.dataPoints
                ?.singleOrNull {
                    it.labels.get("id") == produsent.opplysningsId.toString() &&
                        it.labels.get("navn") == produsent.opplysningsNavn &&
                        it.labels.get("status") == produsent.status.name
                }?.value ?: 0.0

        "samler manglende opplysninger fra produsentinstanser per opplysningstype" {
            val dagsats = Dagsats()
            val dagpengegrunnlag = Dagpengegrunnlag()
            val dagsatsFor = antallManglendeOpplysninger(dagsats)
            val dagpengegrunnlagFor = antallManglendeOpplysninger(dagpengegrunnlag)

            dagsats.vedManglendeOpplysning()
            Dagsats().vedManglendeOpplysning()
            dagpengegrunnlag.vedManglendeOpplysning()

            antallManglendeOpplysninger(dagsats) shouldBe dagsatsFor + 2.0
            antallManglendeOpplysninger(dagpengegrunnlag) shouldBe dagpengegrunnlagFor + 1.0
        }

        "varsler om manglende grunnbelop for grunnlag" {
            val produsent = GrunnbelopForGrunnlag()
            val antallFor = antallManglendeOpplysninger(produsent)
            val kontekst =
                MigreringsKontekst(
                    sakTilMigrering = mockk(),
                    initiertAv = "testbruker",
                    opprettetTidspunkt = LocalDate.of(2026, 11, 1).atStartOfDay(),
                )

            produsent.produser(kontekst).verdi shouldBe null
            antallManglendeOpplysninger(produsent) shouldBe antallFor + 1.0
        }

        "har ikke duplikate UUIDer" {
            val opplysningProdusenter = Opplysninger.produsenter
            val duplikater =
                opplysningProdusenter
                    .groupBy { it.opplysningsId }
                    .filter { it.value.size > 1 }
                    .map { it.key }

            duplikater shouldBe emptyList()
        }

        "bruker typede interface for alle produsenter" {
            val utypedeProdusenter =
                Opplysninger.produsenter.filterNot {
                    it is HeltallsOpplysningProdusent ||
                        it is BoolskOpplysningProdusent ||
                        it is DatoOpplysningProdusent ||
                        it is DesimaltallsOpplysningProdusent ||
                        it is PengeOpplysningProdusent ||
                        it is TekstOpplysningProdusent ||
                        it is InntektsOpplysningProdusent ||
                        it is UlidOpplysningProdusent ||
                        it is BarnOpplysningProdusent
                }

            utypedeProdusenter.map { it.javaClass.simpleName }.sorted() shouldBe emptyList()
        }

        val konverteringer: List<Triple<OpplysningProdusent<*>, OpplysningsverdiDTO, Any>> =
            listOf(
                Triple(Aldersgrense(), HeltallVerdiDTO(67), 67),
                Triple(AvtjentVerneplikt(), BoolskVerdiDTO(false), false),
                Triple(Fodselsdato(), DatoVerdiDTO(LocalDate.of(1980, 1, 1)), LocalDate.of(1980, 1, 1)),
                Triple(OnsketArbeidstid(), DesimaltallVerdiDTO(18.75), 18.75),
                Triple(
                    PensjonFraEnOffentligTjenestepensjonsordningBelop(),
                    PengeVerdiDTO(BigDecimal("1234.56")),
                    BigDecimal("1234.56"),
                ),
                Triple(BruktBeregningsregel(), TekstVerdiDTO("beregningsregel"), "beregningsregel"),
            )

        konverteringer.forEach { (produsent, dto, forventetVerdi) ->
            "bevarer verdi og Kotlin-type fra ${dto::class.simpleName}" {
                val verdi = produsent.verdiFraResultat(dto)

                verdi shouldBe forventetVerdi
                verdi?.javaClass shouldBe forventetVerdi.javaClass
            }

            "avviser andre DTO-typer for ${produsent::class.simpleName}" {
                konverteringer.filter { it.second::class != dto::class }.forEach { (_, feilDto, _) ->
                    shouldThrow<IllegalArgumentException> {
                        produsent.verdiFraResultat(feilDto)
                    }
                }
            }
        }

        "avviser feil datatype selv om DTO-klassen stemmer" {
            val feilaktigeVerdier: List<Pair<OpplysningProdusent<*>, OpplysningsverdiDTO>> =
                listOf(
                    Aldersgrense() to HeltallVerdiDTO(67, datatype = DataTypeDTO.PENGER),
                    AvtjentVerneplikt() to BoolskVerdiDTO(false, datatype = DataTypeDTO.TEKST),
                    Fodselsdato() to DatoVerdiDTO(LocalDate.of(1980, 1, 1), datatype = DataTypeDTO.TEKST),
                    OnsketArbeidstid() to DesimaltallVerdiDTO(18.75, datatype = DataTypeDTO.PENGER),
                    PensjonFraEnOffentligTjenestepensjonsordningBelop() to
                        PengeVerdiDTO(BigDecimal("1234.56"), datatype = DataTypeDTO.DESIMALTALL),
                    BruktBeregningsregel() to TekstVerdiDTO("inntektsreferanse", datatype = DataTypeDTO.INNTEKT),
                )

            feilaktigeVerdier.forEach { (produsent, dto) ->
                shouldThrow<IllegalArgumentException> {
                    produsent.verdiFraResultat(dto)
                }
            }
        }

        "krever egen sammenligning for produsenter med strukturerte verdier" {
            val skalareTyper =
                setOf(
                    Boolean::class,
                    String::class,
                    Int::class,
                    Long::class,
                    Double::class,
                    LocalDate::class,
                    BigDecimal::class,
                )
            val manglendeImplementeringer =
                Opplysninger.produsenter.mapNotNull { produsent ->
                    val klasse = produsent::class
                    val verdiType =
                        klasse.allSupertypes
                            .single { it.classifier == OpplysningProdusent::class }
                            .arguments
                            .single()
                            .type
                    // Kotlin-refleksjon skiller overstyringer fra kompilatorgenererte delegeringer.
                    val harEgenSammenligning =
                        (listOf(klasse) + klasse.allSuperclasses)
                            .filterNot { it == OpplysningProdusent::class }
                            .flatMap { it.declaredMemberFunctions }
                            .any { funksjon ->
                                funksjon.name == "sammenlignVerdier" &&
                                    funksjon.parameters.drop(1).map { it.type.classifier } ==
                                    listOf(Any::class, Any::class)
                            }

                    if (verdiType?.classifier !in skalareTyper && !harEgenSammenligning) {
                        "${klasse.simpleName}<$verdiType>"
                    } else {
                        null
                    }
                }

            withClue("Disse produsentene må overstyre sammenlignVerdier:") {
                manglendeImplementeringer shouldBe emptyList()
            }
        }
    })
