@file:Suppress("SqlResolve")

package no.nav.dagpenger.migrering.arena.innsyn

import java.sql.ResultSet
import javax.sql.DataSource

class ArenaPersonRepository(
    override val dataSource: Lazy<DataSource>,
) : ArenaPersonRepositoryInterface {
    override fun personId(fodselsnummer: String): Int? =
        selectSingle(
            sql = selectPersonIdPåFodselsnummer,
            params = mapOf("fodselsnummer" to fodselsnummer),
        )?.personId

    override fun mapResultat(row: ResultSet): ArenaPerson =
        ArenaPerson(
            fodselsnr = row.getString("fodselsnr"),
            personId = row.getInt("person_id"),
            fornavn = row.getString("fornavn"),
            etternavn = row.getString("etternavn"),
            fodselsdato = row.getDate("fodselsdato")?.toLocalDate(),
        )

    internal val selectPersonIdPåFodselsnummer =
        // language=oracle
        """
        SELECT person_id, fodselsnr, fornavn, etternavn, fodselsdato FROM person 
        WHERE fodselsnr = :fodselsnummer
        """.trimIndent()
}
