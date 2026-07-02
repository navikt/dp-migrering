package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.KvotebrukHendelseResponse
import java.time.LocalDate

data class KvotebrukHendelse(
    val id: Int,
    val kvoteTypeKode: String,
    val endringsGrunnlag: String,
    val antallBevegelse: Int,
    val posteringTypeKode: String,
    val datoHendelse: LocalDate,
    val resterende: Int,
    val modUser: String?,
    val begrunnelse: String?,
) {
    fun tilKontrakt(): KvotebrukHendelseResponse =
        KvotebrukHendelseResponse(
            id = id,
            kvoteTypeKode = kvoteTypeKode,
            endringsGrunnlag = endringsGrunnlag,
            antallBevegelse = antallBevegelse,
            posteringTypeKode = posteringTypeKode,
            datoHendelse = datoHendelse,
            resterende = resterende,
            modUser = modUser,
            begrunnelse = begrunnelse,
        )
}
