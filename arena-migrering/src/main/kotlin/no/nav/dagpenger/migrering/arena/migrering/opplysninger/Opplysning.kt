package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.KClass

// data class Opplysning<T>(
//    val navn: String,
//    val verdi: T?,
//    val gyldigFraOgMed: LocalDate?,
//    val uuid: UUID,
// )

@ConsistentCopyVisibility
data class Opplysning<T : Any>
    @PublishedApi
    internal constructor(
        val navn: String,
        val verdi: T?,
        val gyldigFraOgMed: LocalDate?,
        val uuid: UUID,
        val verdiType: KClass<T>,
    ) {
        companion object {
            inline operator fun <reified T : Any> invoke(
                navn: String,
                verdi: T?,
                gyldigFraOgMed: LocalDate?,
                uuid: UUID,
            ): Opplysning<T> =
                Opplysning(
                    navn = navn,
                    verdi = verdi,
                    gyldigFraOgMed = gyldigFraOgMed,
                    uuid = uuid,
                    verdiType = T::class,
                )
        }
    }
