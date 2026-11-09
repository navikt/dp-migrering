package no.nav.dagpenger.migrering.arena.migrering

import java.util.UUID

internal interface Hendelse {
    fun type(): String

    fun ident(): String

    fun sakId(): Int? = null

    fun migreringId(): UUID

    fun meldingsreferanseId(): UUID
}
