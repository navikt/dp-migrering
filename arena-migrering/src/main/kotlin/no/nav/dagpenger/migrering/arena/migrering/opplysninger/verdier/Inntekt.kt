package no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier

import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntekt
import no.nav.dagpenger.inntekt.v1.KlassifisertInntektMåned
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.node.ObjectNode

typealias InntektV1 = Inntekt

fun Any.tilInntekt(): InntektV1? =
    when (this) {
        is InntektV1 -> this
        is Map<*, *> -> objectMapper.convertValue(this, InntektV1::class.java)
        is ObjectNode -> objectMapper.convertValue(this, InntektV1::class.java)
        else -> null
    }

fun InntektV1.harSammeInnholdSom(other: InntektV1?): Boolean =
    other != null &&
        inntektsId == other.inntektsId &&
        manueltRedigert == other.manueltRedigert &&
        begrunnelseManueltRedigert == other.begrunnelseManueltRedigert &&
        sisteAvsluttendeKalenderMåned == other.sisteAvsluttendeKalenderMåned &&
        // hentetTidspunkt == other.hentetTidspunkt &&
        inntektsListe
            .map { it.harSammeInnholdSom(other.inntektsListe.find { k -> k.årMåned == it.årMåned }) }
            .all { it }

fun KlassifisertInntektMåned.harSammeInnholdSom(other: KlassifisertInntektMåned?): Boolean =
    other != null &&
        årMåned == other.årMåned &&
        harAvvik == other.harAvvik &&
        klassifiserteInntekter
            .map { it.harSammeInnholdSom(other.klassifiserteInntekter.find { k -> k.inntektKlasse == it.inntektKlasse }) }
            .all { it }

fun KlassifisertInntekt.harSammeInnholdSom(other: KlassifisertInntekt?): Boolean =
    other != null &&
        beløp == other.beløp &&
        inntektKlasse == other.inntektKlasse
