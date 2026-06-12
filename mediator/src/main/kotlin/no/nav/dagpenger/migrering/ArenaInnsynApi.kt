package no.nav.dagpenger.migrering

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.dagpenger.migrering.arena.innsyn.ArenaInnsynResponseService

fun Application.arenaInnsynApi(arenaInnsynResponseService: ArenaInnsynResponseService) {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK)
        }

        get("/arena/innsyn/sak/{sakId}/detaljer") {
            val sakId = call.parameters["sakId"]

//            arenaInnsynResponseService.hentArenaSak()?.let {
//                call.respond(HttpStatusCode.OK, it)
//            }
        }
    }
}
