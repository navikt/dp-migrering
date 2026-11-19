package no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier

import no.nav.dagpenger.inntekt.v1.Inntekt
import no.nav.dagpenger.migrering.objectMapper

typealias InntektV1 = Inntekt

fun Any.tilInntekt(): InntektV1 =
    when (this) {
        is InntektV1 -> this
        else -> objectMapper.convertValue(this, InntektV1::class.java)
    }

fun InntektV1.harSammeInnholdSom(other: InntektV1?): Boolean =
    other != null &&
        inntektsId == other.inntektsId &&
        manueltRedigert == other.manueltRedigert &&
        begrunnelseManueltRedigert == other.begrunnelseManueltRedigert &&
        sisteAvsluttendeKalenderMåned == other.sisteAvsluttendeKalenderMåned &&
        hentetTidspunkt == other.hentetTidspunkt &&
        inntektsListe.groupingBy { it }.eachCount() ==
        other.inntektsListe.groupingBy { it }.eachCount()
