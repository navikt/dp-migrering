package no.nav.dagpenger.migrering.db

import ch.qos.logback.core.util.OptionHelper.getEnv
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.flywaydb.core.Flyway

private const val DB_USERNAME_KEY = "DB_USERNAME"
private const val DB_PASSWORD_KEY = "DB_PASSWORD"
private const val DB_URL_KEY = "DB_URL"

// Understands how to create a data source from environment variables
internal class H2DataSourceBuilder {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    private fun getOrThrow(key: String): String = getEnv(key) ?: error("Mangler miljøvariabel $key")

    private val baseConfig =
        HikariConfig().apply {
            // "jdbc:h2:mem:$dbName;MODE=Oracle;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=1"
            jdbcUrl = "jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=1"
            username = "username"
            password = "password"
            driverClassName = "org.h2.Driver" // "oracle.jdbc.OracleDriver"
            isAutoCommit = true
        }

    private val hikariConfig by lazy {
        HikariConfig().apply {
            baseConfig.copyStateTo(this)
            poolName = "app"
            // Default 10
            maximumPoolSize = 10
        }
    }

    val dataSource = lazy { HikariDataSource(hikariConfig) }

    internal fun runMigration() {
        val config =
            HikariConfig().apply {
                baseConfig.copyStateTo(this)
                poolName = "flyway"
                // ideelt skulle vi bare behøvd én connection,
                // men flyway har noen leaks som gjør at den ikke klarer seg med bare én
                maximumPoolSize = 2
            }
        // oppretter egen datasource sånn at vi iallfall lukker den ordentlig,
        // og får returnert connections tilbake til postgres
        HikariDataSource(config).use {
            Flyway
                .configure()
                .locations("classpath:flyway")
                .connectRetries(10)
                .dataSource(it)
                .load()
                .migrate()
        }
    }
}

private fun String.stripCredentials() = this.replace(Regex("://.*:.*@"), "://")

private fun String.ensurePrefix(prefix: String) =
    if (this.startsWith(prefix)) {
        this
    } else {
        prefix + this.substringAfter("//")
    }
