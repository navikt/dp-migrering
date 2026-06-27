package no.nav.dagpenger.migrering.api.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationProvider

class DpTilgangAuthenticationProvider internal constructor(
    config: Config,
) : AuthenticationProvider(config) {
    private companion object {
        val logger = KotlinLogging.logger { }
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        try {
            throw IllegalArgumentException("Tjohei")
        } catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            logger.debug(cause) { "JWT authentication failed: $message" }
            context.error("DpTilgang", AuthenticationFailedCause.Error(message))
        }
    }

    class Config internal constructor(
        name: String?,
    ) : AuthenticationProvider.Config(name) {
        val fnr = "1234"

        internal fun build() = DpTilgangAuthenticationProvider(this)
    }
}
