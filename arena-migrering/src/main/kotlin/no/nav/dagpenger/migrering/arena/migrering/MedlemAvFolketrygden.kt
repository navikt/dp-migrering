package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.util.UUID
import javax.sql.DataSource

class MedlemAvFolketrygden(
    override val opplysnigsId: UUID = UUID.fromString("0194881f-9443-72b4-8b30-5f6cdb24d54c"),
    override val dataSource: Lazy<DataSource>,
) : OpplysningHandler<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override fun håndter(vedtakId: Int): Opplysning<Boolean> {
        val rows =
            select(
                // language=oracle
                """
                SELECT vilkaarstatuskode, vedtak_id, mod_dato 
                FROM vilkaarvurdering 
                WHERE vedtak_id = :vedtakId 
                AND vilkaarkode = 'MEDLFOLKT'
                """.trimIndent(),
                mapOf("vedtakId" to vedtakId),
            ) { row ->
                mapOf(
                    "vilkaarstatuskode" to row.stringOrNull("vilkaarstatuskode"),
                    "vedtak_id" to row.int("vedtak_id"),
                    "mod_dato" to row.localDate("mod_dato"),
                )
            }
        if (rows.size == 1) {
            return Opplysning(
                navn = "Bruker er medlem av folketrygden",
                verdi = verdi(rows.first()["vilkaarstatuskode"] as String?),
                gyldigFraOgMed = rows.first()["mod_dato"] as LocalDate,
                uuid = opplysnigsId,
            )
        }
        throw IllegalArgumentException("Folketrygden er ikke gyldig")
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "J" -> {
                true
            }

            "N" -> {
                false
            }

            else -> {
                null
            }
        }
}
