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
                SELECT vilkaarstatuskode 
                FROM vilkaarvurdering 
                WHERE vedtak_id = :vedtakId 
                AND vilkaarkode = 'MEDLFOLKT'
                """.trimIndent(),
                mapOf("vedtakId" to vedtakId),
            )
        if (rows.size == 1) {
            return Opplysning(
                navn = "Bruker er medlem av folketrygden",
                verdi = verdi(rows.first().getString("vilkaarstatuskode")),
                gyldigFraOgMed = LocalDate.now(),
                uuid = opplysnigsId,
            )
        }
        throw IllegalArgumentException("Folketrygden er ikke gyldig")
    }

    private fun verdi(verdi: String): Boolean? =
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
