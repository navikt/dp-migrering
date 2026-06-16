package no.nav.dagpenger.migrering.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway

internal class H2DataSourceBuilder {
    private val baseConfig =
        HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=1"
            username = "username"
            password = "password"
            driverClassName = "org.h2.Driver"
            isAutoCommit = true
        }

    private val hikariConfig by lazy {
        HikariConfig().apply {
            baseConfig.copyStateTo(this)
            poolName = "app"
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
