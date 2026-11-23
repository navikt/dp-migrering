package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import no.nav.dagpenger.migrering.objectMapper
import java.time.LocalDateTime
import java.util.UUID

internal interface KafkaMeldingV1 {
    val id: UUID
    val navn: String
    val opprettet: LocalDateTime
    val ident: String

    fun toJson(): String = objectMapper.writeValueAsString(this)

    fun publiser(context: MessageContext) {
        context.publish(toJson())
    }

    enum class KafkaMeldingOpprinnelse {
        Intern,
        Ekstern,
        Ukjent,
    }
}
