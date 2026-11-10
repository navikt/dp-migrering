package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import no.nav.dagpenger.migrering.db.OracleDataSource

class ProjectConfig : AbstractProjectConfig() {
    override suspend fun beforeProject() {
        OracleDataSource.start()
    }

    override suspend fun afterProject() {
        OracleDataSource.stop()
    }
}
