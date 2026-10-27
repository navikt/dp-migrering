package no.nav.dagpenger.migrering.arena.migrering.opplysninger

class AntallGjenstaendeDagerForAKunneGjenoppta : OpplysningProdusent<Int> {
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("019d0059-0e34-76a7-ab91-42925a1d03a3")
    override val opplysningsNavn: String = "Antall gjenstående dager for å kunne gjenoppta"

    override suspend fun produser(kontekst: no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst): Opplysning<Int> =
        // TODO: Hva er dette?
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
