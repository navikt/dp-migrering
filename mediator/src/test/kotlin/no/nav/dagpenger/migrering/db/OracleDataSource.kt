package no.nav.dagpenger.migrering.db

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.testcontainers.oracle.OracleContainer
import java.time.Duration

object OracleDataSource {
    private val oracle =
        OracleContainer("gvenzl/oracle-free:23.26.3-slim-faststart")
            .withStartupTimeout(Duration.ofMinutes(3))

    val dataSource =
        lazy {
            HikariDataSource().apply {
                jdbcUrl = oracle.jdbcUrl
                username = oracle.username
                password = oracle.password
                driverClassName = "oracle.jdbc.OracleDriver"
                maximumPoolSize = 2
            }
        }

    fun start() {
        oracle.start()
        runMigration()
    }

    fun stop() {
        try {
            if (dataSource.isInitialized()) {
                dataSource.value.close()
            }
        } finally {
            oracle.stop()
        }
    }

    private fun runMigration() {
        Flyway
            .configure()
            .dataSource(dataSource.value)
            .locations("classpath:flyway/arena")
            .load()
            .migrate()
    }

    fun oracleDataSource() = this.dataSource
}
