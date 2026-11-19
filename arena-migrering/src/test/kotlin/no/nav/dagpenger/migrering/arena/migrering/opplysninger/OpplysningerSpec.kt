package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.prometheus.metrics.model.registry.PrometheusRegistry
import io.prometheus.metrics.model.snapshots.CounterSnapshot
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

        "har ikke duplikate UUIDer" {
            val opplysningProdusenter = Opplysninger.produsenter
            val duplikater =
                opplysningProdusenter
                    .groupBy { it.opplysningsId }
                    .filter { it.value.size > 1 }
                    .map { it.key }

            duplikater shouldBe emptyList()
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
