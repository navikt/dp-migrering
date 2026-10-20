package no.nav.dagpenger.migrering.api

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.arena.innsyn.arenaInnsynApi
import no.nav.dagpenger.migrering.arena.migrering.MigreringsOrchestrator
import no.nav.dagpenger.migrering.arena.migrering.arenaMigreringApi
import javax.sql.DataSource

internal fun Application.authenticationConfig(authFactory: AuthFactory) {
    install(Authentication) {
        jwt("azureAd") {
            with(authFactory) {
                azureAd()
            }
        }
    }
}

internal fun Application.apiConfig(
    authFactory: AuthFactory,
    migreringsOrchestrator: MigreringsOrchestrator,
    dataSource: Lazy<DataSource>,
) {
    arenaInnsynApi(
        authFactory = authFactory,
        dataSource = dataSource,
    )
    arenaMigreringApi(
        authFactory,
        orchestrator = migreringsOrchestrator,
    )
}
