@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.util.UUID

class OppholdINorge(
    override val arenaRepository: ArenaRepository,
) : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9443-72b4-8b30-5f6cdb24d54e")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val rows =
            arenaRepository.select(
                // language=oracle
                """
                SELECT vilkaarstatuskode
                     , vedtak_id
                     , mod_dato 
                FROM   vilkaarvurdering 
                WHERE  vedtak_id = :vedtakId 
                AND    vilkaarkode = 'INORGE'
                """.trimIndent(),
                mapOf("vedtakId" to kontekst.vedtakId),
            ) { row ->
                mapOf(
                    "vilkaarstatuskode" to row.stringOrNull("vilkaarstatuskode"),
                    "vedtak_id" to row.int("vedtak_id"),
                    "mod_dato" to row.localDate("mod_dato"),
                )
            }
        if (rows.size == 1) {
            return Opplysning(
                navn = "Kravet til opphold i Norge er oppfylt",
                verdi = verdi(rows.first()["vilkaarstatuskode"] as String?),
                gyldigFraOgMed = rows.first()["mod_dato"] as LocalDate,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Kravet til opphold i Norge er ikke gyldig")
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
