package no.nav.dagpenger.migrering

import no.nav.dagpenger.migrering.konfigurasjon.Configuration

fun main() {
    ApplicationBuilder(Configuration.config).start()
}
