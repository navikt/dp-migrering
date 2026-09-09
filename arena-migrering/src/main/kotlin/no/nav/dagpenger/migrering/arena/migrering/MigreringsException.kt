package no.nav.dagpenger.migrering.arena.migrering

class MigreringsException(
    string: String,
    e: Exception,
) : Exception(string, e)
