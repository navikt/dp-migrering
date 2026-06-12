package no.nav.dagpenger.migrering.db

import java.sql.ResultSet

fun ResultSet.getIntOrNull(columnLabel: String): Int? {
    val value = getInt(columnLabel)
    return if (wasNull()) null else value
}
