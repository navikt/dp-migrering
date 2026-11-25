package no.nav.dagpenger.migrering.arena.migrering

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger
import no.nav.dagpenger.migrering.db.OracleDataSource.oracleDataSource
import java.time.LocalDateTime
import java.util.UUID

class OpplysningProdusentSpec :
    StringSpec({
        val repository = ArenaMigreringOracleRepository(oracleDataSource())
        "Skal ...." {
            val migreringsdata = repository.hentSakTilMigrering(sakId = 15603478, migreringTidspunkt = LocalDateTime.now())
            val kontekst =
                MigreringsKontekst(
                    sakTilMigrering = migreringsdata,
                    initiertAv = "EnEllerAnnenStreng",
                    migreringsId = UUID.randomUUID(),
                    opprettetTidspunkt = LocalDateTime.now(),
                )
            val opplysninger = Opplysninger.aktiveProdusenter.map { it.produser(kontekst) }
            opplysninger.isEmpty() shouldNotBe true
        }
    })
