package no.dagpenger

import io.prometheus.metrics.core.metrics.Gauge

class ArenaMetrikker {
    companion object {
        val antallDagpengepersoner: Gauge =
            Gauge
                .builder()
                .name("dagpenger_personer_antall")
                .help("Antall unike personer i hvert system som behandler dagpenger, fordelt på om de har rett nå.")
                .labelNames("system", "har_rett")
                .register()
    }
}
