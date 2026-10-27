package no.nav.dagpenger.arena.migrering.api.models

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ToVerdiDTOTypeSpec :
    StringSpec({

        "skal mappe String til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.STRING" {
            val verdi = "test"
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.STRING
        }

        "skal mappe Int til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER" {
            val verdi = 42
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER
        }

        "skal mappe Long til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER" {
            val verdi = 42L
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER
        }

        "skal mappe Float til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT" {
            val verdi = 3.14f
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT
        }

        "skal mappe Double til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT" {
            val verdi = 3.14
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT
        }

        "skal mappe Boolean til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.BOOLEAN" {
            val verdi = true
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.BOOLEAN
        }

        "skal mappe LocalDate til MigreringGrunnlagOpplysningVerdiDTOTypeDTO.DATE" {
            val verdi = java.time.LocalDate.now()
            val type = verdi.toVerdiDTOType()
            type shouldBe MigreringGrunnlagOpplysningVerdiDTOTypeDTO.DATE
        }
    })
