package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

internal object Hendelser {
    val hendelserTilType =
        mapOf(
            MigreringStartetMeldingV1.EVENT_NAME.uppercase() to MigreringStartetMeldingV1::class,
            MigreringSimuleringResultatMeldingV1.EVENT_NAME.uppercase() to MigreringSimuleringResultatMeldingV1::class,
            MigreringSimuleringKontrollResultatMeldingV1.EVENT_NAME.uppercase() to MigreringSimuleringKontrollResultatMeldingV1::class,
            MigreringAvbruttMeldingV1.EVENT_NAME.uppercase() to MigreringAvbruttMeldingV1::class,
        )
}
