package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class Sisteforbruksdato : OpplysningProdusent<LocalDate> {
    override val opplysningsId: UUID = "019cf5f9-b3ef-7464-8be7-a0f8c3d8f7b2".toUUID()
    override val opplysningsNavn: String = "Siste forbruksdato"
    override val aktiv: Boolean = true
    override val rolle: OpplysningProdusent.Rolle = OpplysningProdusent.Rolle.Fakta
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = kontekst.opprettetTidspunkt.toLocalDate(),
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
