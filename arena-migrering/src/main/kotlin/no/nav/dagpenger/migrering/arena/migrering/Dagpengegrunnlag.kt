@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.util.UUID

class Dagpengegrunnlag(
    override val arenaRepository: ArenaRepository,
) : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cbd")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val rows =
            arenaRepository.select(
                // language=oracle
                """
                SELECT vedtakverdi
                     , vedtak_id
                     , mod_dato 
                FROM   vedtakfakta 
                WHERE  vedtak_id = :vedtakId 
                AND    vedtakfaktakode = 'GRUNN'
                """.trimIndent(),
                mapOf("vedtakId" to kontekst.vedtakId),
            ) { row ->
                mapOf(
                    "vedtakverdi" to row.string("vedtakverdi"),
                    "mod_dato" to row.localDate("mod_dato"),
                )
            }
        if (rows.size == 1) {
            return Opplysning(
                navn = "Dagpengegrunnlag",
                verdi = verdi(rows.first()["vedtakverdi"] as String?),
                gyldigFraOgMed = rows.first()["mod_dato"] as LocalDate,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Dagpengegrunnlag er ikke gyldig")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
