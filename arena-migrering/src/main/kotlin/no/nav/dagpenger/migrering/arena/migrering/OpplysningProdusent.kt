package no.nav.dagpenger.migrering.arena.migrering

import java.util.UUID

interface OpplysningProdusent<T> {
    val opplysningsId: UUID

    suspend fun produser(kontekst: MigreringsKontekst): Opplysning<T>
}
