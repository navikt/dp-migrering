package no.nav.dagpenger.migrering.db

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Session
import kotliquery.sessionOf
import javax.sql.DataSource

private val dbLogger = KotlinLogging.logger {}

/**
 * [dataSource] er [Lazy] slik at vi utsetter oppretting av connection pool til første spørring.
 * Da unngår vi at applikasjonen kobler seg til databasen ved oppstart, før den faktisk trengs
 * (f.eks. i tester eller komponenter som ikke alltid leser/skriver).
 */
data class DatabaseSession(
    private val dataSource: Lazy<DataSource>,
) {
    fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)
}
