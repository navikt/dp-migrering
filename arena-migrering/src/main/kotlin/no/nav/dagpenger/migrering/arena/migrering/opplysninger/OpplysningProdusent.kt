package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

interface OpplysningProdusent<T> {
    val opplysningsId: UUID
    val opplysningsNavn: String

    suspend fun produser(kontekst: MigreringsKontekst): Opplysning<T>
}
