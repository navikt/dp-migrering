package no.nav.dagpenger.migrering.konfigurasjon

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import io.github.oshai.kotlinlogging.KotlinLogging

object Configuration {
    private val logger = KotlinLogging.logger { }

    const val APP_NAME = "dp-migrering"

    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "RAPID_APP_NAME" to "dp-migrering",
                "KAFKA_CONSUMER_GROUP_ID" to "dp-migrering-v1",
                "KAFKA_RAPID_TOPIC" to "teamdagpenger.rapid.v1",
                "KAFKA_RESET_POLICY" to "LATEST",
            ),
        )

    object Grupper : PropertyGroup() {
        val saksbehandler by stringType
    }

    data class ArenaDbConfig(
        val jdbcUrl: String,
        val username: String,
        val password: String,
    )

    val arenaDatabaseConfig: ArenaDbConfig
        get() =
            ArenaDbConfig(
                jdbcUrl = properties[Key("ORACLE_URL", stringType)],
                username = properties[Key("ORACLE_USERNAME", stringType)],
                password = properties[Key("ORACLE_PASSWORD", stringType)],
            )

    val properties =
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding defaultProperties

    val config: Map<String, String> =
        properties.list().reversed().fold(emptyMap()) { map, pair ->
            map + pair.second
        }
}
