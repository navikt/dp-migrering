package no.nav.dagpenger.migrering.arena.migrering

import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.db.DatabaseSession
import no.nav.dagpenger.migrering.objectMapper
import org.postgresql.util.PGobject
import java.util.UUID

class MigreringRepositoryPostgres(
    val databaseSession: DatabaseSession,
) : MigreringRepository {
//    private fun <R> session(block: (Session) -> R): R = sessionOf(dataSource.value).use(block)

    override fun lagreMigrering(message: Migrering) {
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
                        SELECT migrering_id, ident, sak_id, initiert_av, opprettet, migrering_tidspunkt
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
                        migreringTidspunkt = row.localDate("migrering_tidspunkt"),
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
                                INSERT INTO migrering_opplysninger (migrering_id, opplysning_id, opplysning)
                                VALUES (:migrering_id, :opplysning_id, CAST(:opplysning AS jsonb))
                                ON CONFLICT (migrering_id, opplysning_id) DO UPDATE SET opplysning = CAST(:opplysning AS jsonb)
                                """.trimIndent(),
                            paramMap =
                                mapOf(
                                    "migrering_id" to migreringId,
                                    "opplysning_id" to opplysning.uuid,
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
                                WHERE migrering_id = :migrering_id AND opplysning_id = :opplysning_id
                                """.trimIndent(),
                            paramMap =
                                mapOf(
                                    "migrering_id" to migreringId,
                                    "opplysning_id" to opplysning.uuid,
                                ),
                        ).asUpdate,
                )
            }
        }
    }

    override fun markereBehandlingOpprettet(
        migreringId: UUID,
        behandlingId: UUID,
    ) {
        databaseSession.session { session ->
            session.run(
                action =
                    queryOf(
                        //language=postgresql
                        statement =
                            """
                            UPDATE migrering
                            SET behandling_id = :behandling_id
                            WHERE migrering_id = :migrering_id
                            """.trimIndent(),
                        paramMap =
                            mapOf(
                                "migrering_id" to migreringId,
                                "behandling_id" to behandlingId,
                            ),
                    ).asUpdate,
            )
        }
    }

    override fun hentManglendeOpplysningerForMigrering(
        migreringId: UUID,
        opplysninger: List<UUID>,
    ): List<MigreringOpplysning> =
        databaseSession.session { session ->
            val opplysningerArray = session.connection.underlying.createArrayOf("uuid", opplysninger.toTypedArray())
            try {
                session.run(
                    queryOf(
                        //language=postgresql
                        statement =
                            """
                            SELECT opplysning, migrert, migrert_tidspunkt
                            FROM migrering_opplysninger
                            WHERE migrering_id = :migrering_id
                            AND (
                                opplysning_id = ANY(:opplysninger)
                                OR migrert IS TRUE
                            )
                            """.trimIndent(),
                        paramMap = mapOf("migrering_id" to migreringId, "opplysninger" to opplysningerArray),
                    ).map { row ->
                        val opplysningJson = row.string("opplysning")
                        MigreringOpplysning(
                            opplysning = objectMapper.readValue(opplysningJson, Opplysning::class.java),
                            migrert = row.boolean("migrert"),
                            migrertTidspunkt = row.localDateTimeOrNull("migrert_tidspunkt"),
                        )
                    }.asList,
                )
            } finally {
                opplysningerArray.free()
            }
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

    override fun lagreMigreringHendelse(
        migreringId: UUID,
        meldingId: UUID,
        meldingType: String,
        ident: String,
        jsonData: String,
        retning: HendelseRetning,
    ) {
        databaseSession.session { session ->
            session.transaction { transactionalSession: TransactionalSession ->
                transactionalSession.run(
                    queryOf(
                        //language=PostgreSQL
                        statement =
                            """
                            INSERT INTO migrering_hendelse
                                (ident, melding_id, migrering_id, melding_type, data, lest_dato, sent_dato)
                            VALUES
                                (:ident, :melding_id, :migrering_id, :melding_type, :data,
                                 CASE WHEN :retning = 'INNGAENDE' THEN NOW() ELSE NULL END,
                                 CASE WHEN :retning = 'UTGAENDE' THEN NOW() ELSE NULL END)
                            ON CONFLICT (melding_id) DO UPDATE SET
                                lest_dato = COALESCE(migrering_hendelse.lest_dato, EXCLUDED.lest_dato),
                                sent_dato = COALESCE(migrering_hendelse.sent_dato, EXCLUDED.sent_dato)
                            """.trimIndent(),
                        paramMap =
                            mapOf(
                                "ident" to ident,
                                "migrering_id" to migreringId,
                                "melding_id" to meldingId,
                                "melding_type" to meldingType,
                                "retning" to retning.name,
                                "data" to
                                    PGobject().apply {
                                        type = "json"
                                        value = jsonData
                                    },
                            ),
                    ).asUpdate,
                )
            }
        }
    }

    private fun hendelsesType(hendelsesType: String): String =
        when (hendelsesType) {
            "migrering_startet" -> "MIGRERING_STARTET"
            else -> hendelsesType.uppercase()
        }
}
