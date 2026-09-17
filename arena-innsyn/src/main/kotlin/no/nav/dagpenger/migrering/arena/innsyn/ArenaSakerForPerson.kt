package no.nav.dagpenger.migrering.arena.innsyn

data class ArenaSakerForPerson(
    val ident: String,
    val saker: List<ArenaSak>,
)
