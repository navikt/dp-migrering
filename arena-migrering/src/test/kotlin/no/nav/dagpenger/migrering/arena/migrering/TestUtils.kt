package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems

fun readResourceAsText(path: String): String =
    object {}.javaClass.getResource(path)?.readText()
        ?: throw IllegalArgumentException("Fant ikke filen: $path")

fun jsonMessageFromResource(path: String): JsonMessage =
    readResourceAsText(path).let {
        JsonMessage(it, MessageProblems(it))
    }
