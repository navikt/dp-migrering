package no.dagpenger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.dagpenger.ArenaMetrikker.Companion.antallDagpengepersoner
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object OppdaterMenneskerMetrikk {
    private val logger = KotlinLogging.logger {}
    private const val SYSTEM = "arena"

    fun start(arenaStatistikkRepository: ArenaStatistikkRepository) {
        logger.info { "Starter OppdaterMenneskerMetrikk" }
        fixedRateTimer(
            name = "Oppdater personer-metrikk",
            daemon = true,
            initialDelay = Random.nextInt(60, 180).seconds.inWholeMilliseconds,
            period = 15.minutes.inWholeMilliseconds,
            action = {
                try {
                    val antallPerStatus = arenaStatistikkRepository.tellMenneskerPerRettighetstatus()
                    antallDagpengepersoner.labelValues(SYSTEM, "true").set((antallPerStatus[true] ?: 0L).toDouble())
                    antallDagpengepersoner.labelValues(SYSTEM, "false").set((antallPerStatus[false] ?: 0L).toDouble())
                    logger.info { "Oppdatering av personer-metrikk fullført" }
                    logger.info { "Antall personer med rett på dagpenger: ${antallPerStatus[true] ?: 0L}" }
                    logger.info { "Antall personer uten rett på dagpenger: ${antallPerStatus[false] ?: 0L}" }
                } catch (e: Exception) {
                    logger.error(e) { "Oppdatering av personer-metrikk feilet" }
                }
            },
        )
    }
}
