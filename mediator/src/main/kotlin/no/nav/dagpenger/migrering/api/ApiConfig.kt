package no.nav.dagpenger.migrering.api

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.arena.innsyn.arenaInnsynApi

internal fun Application.authenticationConfig(authFactory: AuthFactory) {
    install(Authentication) {
        jwt("azureAd") {
            with(authFactory) {
                azureAd()
            }
        }
//        jwt("admin") {
//            with(authFactory) {
//                adminTilgang()
//            }
//        }
    }
}

internal fun Application.apiConfig(authFactory: AuthFactory) {
    arenaInnsynApi(authFactory)
}
