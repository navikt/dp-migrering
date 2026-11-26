package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.behandling.api.models.BarnVerdiDTO
import no.nav.dagpenger.behandling.api.models.BarnVerdiDTOKildeDTO
import no.nav.dagpenger.behandling.api.models.BarnelisteDTO
import no.nav.dagpenger.behandling.api.models.BoolskVerdiDTO
import no.nav.dagpenger.behandling.api.models.DataTypeDTO
import no.nav.dagpenger.behandling.api.models.DatoVerdiDTO
import no.nav.dagpenger.behandling.api.models.DesimaltallVerdiDTO
import no.nav.dagpenger.behandling.api.models.HeltallVerdiDTO
import no.nav.dagpenger.behandling.api.models.OpplysningsverdiDTO
import no.nav.dagpenger.behandling.api.models.PengeVerdiDTO
import no.nav.dagpenger.behandling.api.models.TekstVerdiDTO
import no.nav.dagpenger.behandling.api.models.UlidVerdiDTO
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.Barn
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.Barnekilde
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.harSammeInnholdSom
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.tilInntekt
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

interface DtoOpplysningProdusent<T : Any> : OpplysningProdusent<T> {
    fun verdiFraDto(verdi: OpplysningsverdiDTO): T

    override fun verifiserDto(
        verdi: OpplysningsverdiDTO,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningVerifisertResultat =
        verifiser(
            verdi = verdiFraDto(verdi),
            migreringId = migreringId,
            repository = repository,
        )
}

interface HeltallsOpplysningProdusent : DtoOpplysningProdusent<Int> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): Int {
        require(verdi is HeltallVerdiDTO && verdi.datatype == DataTypeDTO.HELTALL) {
            "Opplysning $opplysningsId forventer datatype heltall"
        }
        return verdi.verdi
    }
}

interface BoolskOpplysningProdusent : DtoOpplysningProdusent<Boolean> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): Boolean {
        require(verdi is BoolskVerdiDTO && verdi.datatype == DataTypeDTO.BOOLSK) {
            "Opplysning $opplysningsId forventer datatype boolsk"
        }
        return verdi.verdi
    }
}

interface DatoOpplysningProdusent : DtoOpplysningProdusent<LocalDate> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): LocalDate {
        require(verdi is DatoVerdiDTO && verdi.datatype == DataTypeDTO.DATO) {
            "Opplysning $opplysningsId forventer datatype dato"
        }
        return verdi.verdi
    }
}

interface DesimaltallsOpplysningProdusent : DtoOpplysningProdusent<Double> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): Double {
        require(verdi is DesimaltallVerdiDTO && verdi.datatype == DataTypeDTO.DESIMALTALL) {
            "Opplysning $opplysningsId forventer datatype desimaltall"
        }
        return verdi.verdi
    }
}

interface PengeOpplysningProdusent : DtoOpplysningProdusent<BigDecimal> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): BigDecimal {
        require(verdi is PengeVerdiDTO && verdi.datatype == DataTypeDTO.PENGER) {
            "Opplysning $opplysningsId forventer datatype penger"
        }
        return verdi.verdi
    }
}

interface TekstOpplysningProdusent : DtoOpplysningProdusent<String> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): String {
        require(verdi is TekstVerdiDTO && verdi.datatype == DataTypeDTO.TEKST) {
            "Opplysning $opplysningsId forventer datatype tekst"
        }
        return verdi.verdi
    }
}

interface InntektsOpplysningProdusent : OpplysningProdusent<InntektV1> {
    override fun sammenlignVerdier(
        arenaVerdi: Any?,
        dpSakVerdi: Any?,
    ): Boolean {
        val arenaInntekt = arenaVerdi?.tilInntekt()
        val dpSakInntekt = dpSakVerdi?.tilInntekt()
        return dpSakInntekt?.harSammeInnholdSom(arenaInntekt) ?: false
    }

    override fun verifiserDto(
        verdi: OpplysningsverdiDTO,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningVerifisertResultat {
        // Behandlingsapi-et sender inntektsreferanser som tekstverdier.
        require(
            verdi is TekstVerdiDTO &&
                (verdi.datatype == DataTypeDTO.INNTEKT || verdi.datatype == DataTypeDTO.TEKST),
        ) {
            "Opplysning $opplysningsId forventer en inntektsreferanse"
        }
        val arenaOpplysning = repository.hentOpplysning(migreringId, opplysningsId)
        val arenaInntekt =
            arenaOpplysning?.verdi?.let {
                requireNotNull(it.tilInntekt()) {
                    "Opplysning $opplysningsId forventer InntektV1"
                }
            }
        val arenaInntektsId = arenaInntekt?.inntektsId

        return OpplysningVerifisertResultat(
            uuid = opplysningsId,
            navn = opplysningsNavn,
            verifisert = arenaOpplysning?.uuid == opplysningsId && arenaInntektsId == verdi.verdi,
            dpSakVerdi = verdi.verdi,
            arenaVerdi = arenaInntektsId,
        )
    }
}

interface UlidOpplysningProdusent : DtoOpplysningProdusent<String> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): String {
        require(verdi is UlidVerdiDTO && verdi.datatype == DataTypeDTO.ULID) {
            "Opplysning $opplysningsId forventer datatype ulid"
        }
        return verdi.verdi
    }
}

interface BarnOpplysningProdusent : DtoOpplysningProdusent<BarnListe> {
    override fun verdiFraDto(verdi: OpplysningsverdiDTO): BarnListe {
        require(verdi is BarnelisteDTO && verdi.datatype == DataTypeDTO.BARN) {
            "Opplysning $opplysningsId forventer datatype barn"
        }
        return BarnListe(
            søknadbarnId = verdi.søknadBarnId,
            barn = verdi.verdi.map { it.tilBarn() },
        )
    }
}

@Suppress("DEPRECATION")
private fun BarnVerdiDTO.tilBarn(): Barn =
    Barn(
        kilde =
            when (kilde) {
                BarnVerdiDTOKildeDTO.REGISTER -> Barnekilde.Register
                BarnVerdiDTOKildeDTO.SØKNAD -> Barnekilde.Søknad
                BarnVerdiDTOKildeDTO.SAKSBEHANDLER -> Barnekilde.Saksbehandler
                null -> null
            },
        ident = ident,
        fødselsdato = fødselsdato,
        fornavnOgMellomnavn = fornavnOgMellomnavn,
        etternavn = etternavn,
        statsborgerskap = statsborgerskap,
        oppholdsland = oppholdsland,
        kvalifiserer = kvalifiserer,
        forsørgeransvar = forsørgeransvar,
        begrunnelse = begrunnelse,
    )
