@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaTelleverkRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaTelleverkRepositoryInterface {
    override fun hentTelleverkForPerson(personId: PersonId): TelleverkForPerson {
        val kvoteVerdier = select(selectTelleverkPåPerson, mapOf("personId" to personId.id))

        return TelleverkForPerson(
            dagpengePeriodeTeller = kvoteVerdier.firstOrNull { it.kode == "DPTEL" }?.verdi ?: 0,
            maxPeriodePermittertTellerFisk = kvoteVerdier.firstOrNull { it.kode == "MAXFT" }?.verdi ?: 0,
            maxPeriodePermittertTeller = kvoteVerdier.firstOrNull { it.kode == "MAXPT" }?.verdi ?: 0,
        )
    }

    override fun mapResultat(row: ResultSet): KvoteVerdi =
        KvoteVerdi(
            kode = row.getString("beregningsleddkode"),
            verdi = row.getInt("verdi"),
        )

    internal val selectTelleverkPåPerson =
        // language=oracle
        """
        SELECT b.verdi, b.beregningsleddkode
        FROM BEREGNINGSLEDD b
        WHERE b.tabellnavnalias_kilde = 'KVOTBR'
          AND b.person_id = :personId
          AND b.beregningsleddkode IN ('DPTEL', 'MAXFT', 'MAXPT')
        """.trimIndent()
}
