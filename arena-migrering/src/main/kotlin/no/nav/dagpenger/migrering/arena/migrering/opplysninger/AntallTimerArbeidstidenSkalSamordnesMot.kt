package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class AntallTimerArbeidstidenSkalSamordnesMot : OpplysningProdusent<Double> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Double> = Double::class.java
    override val opplysningsId: UUID = UUID.fromString("0194881f-9433-70e9-a85b-c246150c45d1")
    override val opplysningsNavn: String = "Uføretrygd etter lovens kapittel 12"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
