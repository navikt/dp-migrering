package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

class OppgittAndreYtelserUtenforNav : OpplysningProdusent<Boolean> {
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-942e-7cb0-aa59-05ea449d88e0")
    override val opplysningsNavn: String = "Oppgitt andre ytelser utenfor NAV"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
