package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDate
import java.util.UUID
import javax.sql.DataSource

class OppfyllerKravetTilAlder(
    override val dataSource: Lazy<DataSource>,
    override val opplysnigsId: UUID = UUID.fromString("0194881f-940b-76ff-acf5-ba7bcb367237"),
) : OpplysningHandler<Boolean> {
    override fun håndter(vedtakId: Int): Opplysning<Boolean> {
        select("select * from person where 1 = 0", emptyMap())
        return Opplysning(
            navn = "Oppfyller krav til alder",
            verdi = true,
            gyldigFraOgMed = LocalDate.now(),
            uuid = opplysnigsId,
        )
    }
}
