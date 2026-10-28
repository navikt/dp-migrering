package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import java.util.UUID
import javax.sql.DataSource

class MigreringRepositoryPostgres(
    val dataSource: Lazy<DataSource>,
) : MigreringRepository {
    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    override fun lagre(message: JsonMessage) {
        session { session ->
            session.run(
                action =
                    queryOf(
                        statement =
                            """
                            INSERT INTO migrering (id, ident, sak_id, initiert_av, hendelse, melding)
                            VALUES (:migreringId, :ident, :sakId, :initiertAv, :hendelse, :melding)
                            """.trimIndent(),
                        paramMap =
                            mapOf(
                                "migreringId" to UUID.fromString(message["migreringId"].asString()),
                                "sakId" to message["sakId"].asInt(),
                                "initiertAv" to message["initiertAv"].asString(),
                                "hendelse" to message["@event_name"],
                                "melding" to message.toJson(),
                            ),
                    ).asUpdate,
            )
        }
    }

    override fun hentMigrering(saksnummer: String) {
        TODO("Not yet implemented")
    }

    override fun hentMigrering(migreringId: UUID) {
        TODO("Not yet implemented")
    }
}
