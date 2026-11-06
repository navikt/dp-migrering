package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.queryOf
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.db.DatabaseSession
import no.nav.dagpenger.migrering.objectMapper
import java.util.UUID

class MigreringRepositoryPostgres(
    val databaseSession: DatabaseSession,
) : MigreringRepository {
//    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    override fun lagre(message: Migrering) {
        databaseSession.session { session ->
            session.run(
                action =
                    queryOf(
                        //language=postgresql
                        statement =
                            """
                            INSERT INTO migrering (migrering_id, ident, sak_id, initiert_av)
                            VALUES (:migrering_id, :ident, :sak_id, :initiert_av)
                            """.trimIndent(),
                        paramMap =
                            mapOf(
                                "migrering_id" to message.migreringId,
                                "ident" to message.ident,
                                "sak_id" to message.sakId,
                                "initiert_av" to message.initiertAv,
                            ),
                    ).asUpdate,
            )
        }
    }

    override fun hentMigrering(migreringId: UUID): Migrering? =
        databaseSession.session { session ->
            session.run(
                queryOf(
                    //language=postgresql
                    statement =
                        """
                        SELECT migrering_id, ident, sak_id, initiert_av, opprettet
                        FROM migrering
                        WHERE migrering_id = :migrering_id
                        """.trimIndent(),
                    paramMap = mapOf("migrering_id" to migreringId),
                ).map { row ->
                    Migrering(
                        migreringId = row.uuid("migrering_id"),
                        ident = row.string("ident"),
                        sakId = row.int("sak_id"),
                        initiertAv = row.string("initiert_av"),
                        opprettet = row.localDateTime("opprettet"),
                    )
                }.asSingle,
            )
        }

    override fun lagreOpplysninger(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    ) {
        databaseSession.transaction {
            opplysninger.forEach { opplysning ->
                session.run(
                    action =
                        queryOf(
                            //language=postgresql
                            statement =
                                """
                                INSERT INTO migrering_opplysninger (migrering_id, opplysninger_id, opplysning)
                                VALUES (:migrering_id, :opplysninger_id, CAST(:opplysning AS jsonb))
                                ON CONFLICT (migrering_id, opplysninger_id) DO UPDATE SET opplysning = CAST(:opplysning AS jsonb)
                                """.trimIndent(),
                            paramMap =
                                mapOf(
                                    "migrering_id" to migreringId,
                                    "opplysninger_id" to opplysning.uuid,
                                    "opplysning" to objectMapper.writeValueAsString(opplysning),
                                ),
                        ).asUpdate,
                )
            }
        }
    }

    override fun markereOpplysningerSomMigrert(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    ) {
        databaseSession.transaction {
            opplysninger.forEach { opplysning ->
                session.run(
                    action =
                        queryOf(
                            //language=postgresql
                            statement =
                                """
                                UPDATE migrering_opplysninger
                                SET migrert = true, migrert_tidspunkt = NOW()
                                WHERE migrering_id = :migrering_id AND opplysninger_id = :opplysninger_id
                                """.trimIndent(),
                            paramMap =
                                mapOf(
                                    "migrering_id" to migreringId,
                                    "opplysninger_id" to opplysning.uuid,
                                ),
                        ).asUpdate,
                )
            }
        }
    }

    override fun hentManglendeOpplysningerForMigrering(
        migreringId: UUID,
        opplysninger: List<UUID>,
    ): List<MigreringOpplysning> =
        databaseSession.session { session ->
            session.run(
                queryOf(
                    //language=postgresql
                    statement =
                        """
                        SELECT opplysning, migrert, migrert_tidspunkt
                        FROM migrering_opplysninger
                        WHERE migrering_id = :migrering_id
                        AND (
                            opplysninger_id = ANY(:opplysninger)
                            OR migrert IS TRUE
                        )
                        """.trimIndent(),
                    paramMap = mapOf("migrering_id" to migreringId, "opplysninger" to opplysninger),
                ).map { row ->
                    val opplysningJson = row.string("opplysning")
                    MigreringOpplysning(
                        opplysning = objectMapper.readValue(opplysningJson, Opplysning::class.java),
                        migrert = row.boolean("migrert"),
                        migrertTidspunkt = row.localDateTimeOrNull("migrert_tidspunkt"),
                    )
                }.asList,
            )
        }

    override fun hentOpplysningerForMigrering(migreringId: UUID): List<MigreringOpplysning> =
        databaseSession.session { session ->
            session.run(
                queryOf(
                    //language=postgresql
                    statement =
                        """
                        SELECT opplysning, migrert, migrert_tidspunkt
                        FROM migrering_opplysninger
                        WHERE migrering_id = :migrering_id
                        """.trimIndent(),
                    paramMap = mapOf("migrering_id" to migreringId),
                ).map { row ->
                    val opplysningJson = row.string("opplysning")
                    MigreringOpplysning(
                        opplysning = objectMapper.readValue(opplysningJson, Opplysning::class.java),
                        migrert = row.boolean("migrert"),
                        migrertTidspunkt = row.localDateTimeOrNull("migrert_tidspunkt"),
                    )
                }.asList,
            )
        }
}
