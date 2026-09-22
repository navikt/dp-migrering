package no.nav.dagpenger.migrering.konfigurasjon

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.readText

object Configuration {
    private val logger = KotlinLogging.logger { }

    const val APP_NAME = "dp-migrering"

    private val defaultProperties =
        ConfigurationMap(
            mapOf(),
        )

    object Grupper : PropertyGroup() {
        val saksbehandler by stringType
    }

    data class ArenaDbConfig(
        val jdbcUrl: String,
        val username: String,
        val password: String,
    )

    val Configuration.arenaDatabaseConfig: ArenaDbConfig
        get() =
            ArenaDbConfig(
                kotlin.runCatching { Path(this[Key("DB_JDBC_URL_PATH", stringType)]).readText() }.getOrElse {
                    logger.warn(it) { "Could not read DB_JDBC_URL_PATH " }
                    "localhost"
                },
                kotlin.runCatching { Path(this[Key("DB_USERNAME_PATH", stringType)]).readText() }.getOrElse {
                    logger.warn(it) { "Could not read DB_USERNAME_PATH " }
                    "username"
                },
                kotlin.runCatching { Path(this[Key("DB_PASSWORD_PATH", stringType)]).readText() }.getOrElse {
                    logger.warn(it) { "Could not read DB_PASSWORD_PATH " }
                    "password"
                },
            )

    val properties =
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding defaultProperties

    val config: Map<String, String> =
        properties.list().reversed().fold(emptyMap()) { map, pair ->
            map + pair.second
        }
}
