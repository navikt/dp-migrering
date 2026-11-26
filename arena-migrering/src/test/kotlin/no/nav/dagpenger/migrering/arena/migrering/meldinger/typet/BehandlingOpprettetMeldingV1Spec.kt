package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.migrering.arena.migrering.meldinger.BehandlingOpprettetMeldingV1

class BehandlingOpprettetMeldingV1Spec :
    StringSpec({

        "skal kunne deserialisere melding" {
            val packet = JsonMessage(meldingJson, MessageProblems(meldingJson))
            val melding = BehandlingOpprettetMeldingV1.fra(packet)

            melding.id shouldBe "4640675e-8d6a-491d-b2c9-d52f07822e0a".toUUID()
            melding.ident shouldBe "26418531931"
            melding.migreringId shouldBe "01a1eb26-143b-770d-b2d0-e5cdd420332b".toUUID()
        }
    })

// language=json
private val meldingJson =
    """
    {
      "@id": "4640675e-8d6a-491d-b2c9-d52f07822e0a",
      "ident": "26418531931",
      "regelverk": "Dagpenger",
      "@opprettet": "2026-11-17T21:15:04.527783234",
      "@event_name": "behandling_opprettet",
      "behandlingId": "01a1eb26-1741-7236-8b6b-876d467255ed",
      "@forårsaket_av": {
        "id": "d8067ad3-0870-4d37-8521-e9b63c4da2c4",
        "opprettet": "2026-11-17T21:15:04.265975275",
        "event_name": "migrering_opplysninger"
      },
      "behandletHendelse": {
        "id": "01a1eb26-143b-770d-b2d0-e5cdd420332b",
        "type": "Migrering",
        "skjedde": "2026-11-17",
        "datatype": "UUID"
      },
      "system_read_count": 1,
      "behandlingskjedeId": "01a1eb26-1741-7236-8b6b-876d467255ed",
      "basertPåBehandlinger": [
        "null"
      ],
      "system_participating_services": [
        {
          "id": "4640675e-8d6a-491d-b2c9-d52f07822e0a",
          "time": "2026-11-17T21:15:04.527783234",
          "image": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-behandling:2026.11.17-17.27-48d1602@sha256:5a9c9a248ed75259e8f61727626afbcaf85ba66f1ce91c778559c730051b6dc6",
          "service": "dp-behandling",
          "instance": "dp-behandling-65bd654bb5-bgs4g"
        },
        {
          "id": "4640675e-8d6a-491d-b2c9-d52f07822e0a",
          "time": "2026-11-17T21:15:05.320913621",
          "image": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-migrering:2026.11.17-19.48-aeda535@sha256:e193276a24b95af974bd314f0ad3708f699652cd9031f8c2c81f914abe843fc3",
          "service": "dp-migrering",
          "instance": "dp-migrering-5d767b5559-xznhr"
        }
      ]
    }
    """.trimIndent()
