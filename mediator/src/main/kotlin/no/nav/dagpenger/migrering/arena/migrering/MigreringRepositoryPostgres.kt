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
        hendelse: String,
        ident: String,
        jsonData: String,
        opprinnelse: String,
    ) {
        databaseSession.session { session ->
            session.transaction { transactionalSession: TransactionalSession ->
                // Lås før sekvens-ID tildeles, slik at samtidige skriv for samme migrering committes i ID-rekkefølge.
                transactionalSession.run(
                    queryOf(
                        "SELECT migrering_id FROM migrering WHERE migrering_id = :migrering_id FOR UPDATE",
                        mapOf("migrering_id" to migreringId),
                    ).map { it.uuid("migrering_id") }.asSingle,
                )
                transactionalSession.run(
                    queryOf(
                        //language=PostgreSQL
                        statement =
                            """
                            INSERT INTO migrering_hendelse
                                (ident, melding_id, migrering_id, hendelse, data, tidspunkt, opprinnelse)
                            VALUES
                                (:ident, :melding_id, :migrering_id, :hendelse, :data, NOW(), :opprinnelse)
                                ON CONFLICT DO NOTHING 
                            """.trimIndent(),
                        paramMap =
                            mapOf(
                                "ident" to ident,
                                "melding_id" to meldingId,
                                "migrering_id" to migreringId,
                                "hendelse" to hendelse,
                                "opprinnelse" to opprinnelse,
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

    override fun migreringHendelser(
        migreringId: UUID,
        lastEventId: Long,
        limit: Int,
    ): List<MigreringHendelse> =
        databaseSession.session { session ->
            session.run(
                queryOf(
                    //language=PostgreSQL
                    statement =
                        """
                        SELECT h.id, h.ident, h.melding_id, h.migrering_id, h.hendelse, h.data, h.tidspunkt, h.opprinnelse,
                               m.sak_id, m.initiert_av
                        FROM migrering_hendelse h
                        JOIN migrering m ON m.migrering_id = h.migrering_id
                        WHERE h.migrering_id = :migrering_id AND h.id > :last_event_id
                        ORDER BY h.id ASC
                        LIMIT :limit
                        """.trimIndent(),
                    paramMap = mapOf("migrering_id" to migreringId, "last_event_id" to lastEventId, "limit" to limit),
                ).map { row ->
                    val dataJson = row.string("data")
                    MigreringHendelse(
                        id = row.long("id"),
                        ident = row.string("ident"),
                        meldingId = row.uuid("melding_id"),
                        migreringId = row.uuid("migrering_id"),
                        hendelse = row.string("hendelse"),
                        sakId = row.int("sak_id"),
                        initiertAv = row.string("initiert_av"),
                        data = dataJson,
                        tidspunkt = row.localDateTime("tidspunkt"),
                        opprinnelse = row.string("opprinnelse"),
                    )
                }.asList,
            )
        }
}
