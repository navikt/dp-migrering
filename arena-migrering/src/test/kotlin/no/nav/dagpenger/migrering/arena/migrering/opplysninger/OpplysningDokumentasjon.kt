package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import com.spun.util.persistence.Loader
import io.kotest.core.spec.style.StringSpec
import org.approvaltests.Approvals
import org.approvaltests.core.Options
import org.approvaltests.namer.NamerWrapper
import java.nio.file.Paths

class OpplysningDokumentasjon : StringSpec() {
    private companion object {
        val path = "${Paths.get("").toAbsolutePath().toString().substringBeforeLast("/")}/docs/"
        val options: Options? = Options().forFile().withExtension(".md")
    }

    init {
        "OpplysningDokumentasjon" {
            val markdown =
                StringBuilder(
                    """
            ># Dokumentasjon av opplysninger
            >
            >""".trimMargin(">"),
                )

            markdown.appendLine(
                """
                >#### Opplysninger
                >|UUID|Navn|Arena kilde|Rolle|Aktiv|
                >|---|---|---|---|---|
                """.trimMargin(">"),
            )

            Opplysninger.produsenter.forEach { produsent ->
                val id = produsent.opplysningsId
                val navn = produsent.opplysningsNavn
                val rolle = produsent.rolle.name
                val aktiv = produsent.aktiv

                markdown.appendLine(
                    """
                        >|$id|$navn|-|$rolle|$aktiv|
                        """.trimMargin(">").trimIndent(),
                )
            }

            skriv(
                "Opplysninger",
                markdown.toString(),
            )
        }
    }

    fun skriv(
        filnavn: String,
        innhold: String,
    ) {
        Approvals.namerCreater = Loader { NamerWrapper({ filnavn }, { path }) }
        Approvals.verify(innhold, options)
    }
}
