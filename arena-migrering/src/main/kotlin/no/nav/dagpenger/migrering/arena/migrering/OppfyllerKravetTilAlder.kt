package no.nav.dagpenger.migrering.arena.migrering

import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource

class OppfyllerKravetTilAlder(
    override val dataSource: Lazy<DataSource>,
    override val opplysnigsId: UUID = UUID.fromString("0194881f-940b-76ff-acf5-ba7bcb367237"),
) : OpplysningHandler<Boolean> {
    override fun mapResultat(row: ResultSet): Opplysning<Boolean> =
        Opplysning(
            navn = "Oppfyller krav til alder",
            verdi = row.getBoolean("verdi"),
            gyldigFraOgMed = row.getString("gyldig_fra_og_med"),
            uuid = opplysnigsId,
        )

    override fun håndter(): Opplysning<Boolean> = select("select * from tabel", emptyMap())
}
