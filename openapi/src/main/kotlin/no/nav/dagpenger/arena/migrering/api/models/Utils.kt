package no.nav.dagpenger.arena.migrering.api.models

import java.time.LocalDate

fun Any.toVerdiDTOType(): MigreringGrunnlagOpplysningVerdiDTOTypeDTO =
    when (this) {
        is String -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.STRING
        }

        is Int, is Long -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER
        }

        is Float, is Double, is java.math.BigDecimal -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT
        }

        is Boolean -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.BOOLEAN
        }

        is LocalDate -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.DATE
        }

        else -> {
            error("Ustøttet verditype: ${this::class.qualifiedName}")
        }
    }
