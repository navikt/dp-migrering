package no.nav.dagpenger.migrering.arena.migrering

internal interface Hendelse {
    fun navn(): String

    fun opprinnelse(): Opprinnelse

    enum class Opprinnelse {
        Intern,
        Ekstern,
    }
}
