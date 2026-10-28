package no.nav.dagpenger.migrering.db

import kotliquery.Session

data class PostgresUnitOfWork(
    val session: Session,
)
