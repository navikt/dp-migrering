package no.nav.dagpenger.migrering.arena.migrering.opplysninger

class KravTilProsentvisTapAvArbeidstid : OpplysningProdusent<Double> {
    override val opplysningsId: java.util.UUID = java.util.UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a762")
    override val opplysningsNavn: String = "Krav til prosentvis tap av arbeidstid"

    override suspend fun produser(kontekst: no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst): Opplysning<Double> {
        val opplysning = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "TARBTID" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi?.toDouble(),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
