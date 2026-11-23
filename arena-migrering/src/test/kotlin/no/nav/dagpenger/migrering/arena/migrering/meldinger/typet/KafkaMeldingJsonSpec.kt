package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import tools.jackson.databind.node.ObjectNode

class KafkaMeldingJsonSpec :
    StringSpec({
//        val migrering =
//            Migrering(
//                ident = "12345678901",
//                sakId = 123456,
//                initiertAv = "test",
//                migreringTidspunkt = LocalDate.of(2026, 11, 19),
//                simulering = true,
//            )
//        val startet = MigreringStartetMeldingV1.fra(migrering)
//        val verifisert =
//            OpplysningVerifisertResultat(
//                uuid = UUID.randomUUID(),
//                navn = "opplysning",
//                verifisert = true,
//                dpSakVerdi = "test",
//                arenaVerdi = "test",
//            )
//        val resultat =
//            MigreringSimuleringKontrollResultatMeldingV1.fra(
//                kilde = startet,
//                verifisert = listOf(verifisert),
//                ikkeVerifisert = listOf(verifisert.copy(verifisert = false)),
//            )
//
//        "skal viderefore migreringsfeltene og opprette ny meldingsmetadata" {
//            resultat.migreringId shouldBe migrering.migreringId
//            resultat.ident shouldBe migrering.ident
//            resultat.sakId shouldBe migrering.sakId
//            resultat.initiertAv shouldBe migrering.initiertAv
//            resultat.migreringTidspunkt shouldBe migrering.migreringTidspunkt
//            resultat.simulering shouldBe migrering.simulering
//            resultat.id shouldNotBe startet.id
//            resultat.navn shouldBe MigreringSimuleringKontrollResultatMeldingV1.EVENT_NAME
//        }
//
//        "skal lese og skrive typede meldinger med flatt JSON-format" {
//            val json = KafkaMeldingJson.skriv(resultat)
//            val node = objectMapper.readTree(json.toJson())
//
//            node["@id"].asString() shouldBe resultat.id.toString()
//            node["@event_name"].asString() shouldBe resultat.navn
//            LocalDateTime.parse(node["@opprettet"].asString()) shouldBe resultat.opprettet
//            node["migreringId"].asString() shouldBe migrering.migreringId.toString()
//            node.has("id") shouldBe false
//            node.has("navn") shouldBe false
//            node.has("opprettet") shouldBe false
//            KafkaMeldingJson.lesKontrollresultat(json) shouldBe resultat
//            KafkaMeldingJson.lesStartet(KafkaMeldingJson.skriv(startet)) shouldBe startet
//        }
//
//        "skal ignorere ekstra metadata uten a endre de typede feltene" {
//            val node = objectMapper.valueToTree<ObjectNode>(resultat)
//            node.put("@ekstra", "metadata")
//
//            KafkaMeldingJson.lesKontrollresultat(node.toPacket()) shouldBe resultat
//        }
//
//        "skal bruke false nar simulering mangler" {
//            val node = objectMapper.valueToTree<ObjectNode>(resultat)
//            node.remove("simulering")
//
//            KafkaMeldingJson.lesKontrollresultat(node.toPacket()) shouldBe resultat.copy(simulering = false)
//        }
//
//        "skal avvise manglende obligatoriske felter" {
//            listOf("@event_name", "ident", "migreringId", "sakId", "verifisert").forEach { felt ->
//                val node = objectMapper.valueToTree<ObjectNode>(resultat)
//                node.remove(felt)
//
//                shouldThrow<JacksonException> {
//                    KafkaMeldingJson.lesKontrollresultat(node.toPacket())
//                }
//            }
//        }
//
//        "skal bruke metadata fra JsonMessage nar rammeverket oppretter dem" {
//            val node = objectMapper.valueToTree<ObjectNode>(resultat)
//            node.remove("@id")
//            node.remove("@opprettet")
//            val packet = node.toPacket()
//            val melding = KafkaMeldingJson.lesKontrollresultat(packet)
//            val json = objectMapper.readTree(packet.toJson())
//
//            melding.id.toString() shouldBe json["@id"].asString()
//            melding.opprettet shouldBe LocalDateTime.parse(json["@opprettet"].asString())
//        }
//
//        "skal avvise null i primitive felter" {
//            listOf("sakId", "simulering").forEach { felt ->
//                val node = objectMapper.valueToTree<ObjectNode>(resultat)
//                node.putNull(felt)
//
//                shouldThrow<JacksonException> {
//                    KafkaMeldingJson.lesKontrollresultat(node.toPacket())
//                }
//            }
//        }
//
//        "skal avvise feil eventnavn og ugyldig UUID" {
//            val feilNavn = objectMapper.valueToTree<ObjectNode>(resultat)
//            feilNavn.put("@event_name", BehandlingOpprettetMeldingV1.EVENT_NAME)
//            val feilId = objectMapper.valueToTree<ObjectNode>(resultat)
//            feilId.put("@id", "ugyldig")
//
//            listOf(feilNavn, feilId).forEach { node ->
//                shouldThrow<JacksonException> {
//                    KafkaMeldingJson.lesKontrollresultat(node.toPacket())
//                }
//            }
//        }
//
//        "skal representere behandling opprettet uten a kreve migreringsfelter" {
//            val melding =
//                BehandlingOpprettetMeldingV1(
//                    id = UUID.randomUUID(),
//                    navn = BehandlingOpprettetMeldingV1.EVENT_NAME,
//                    opprettet = LocalDateTime.of(2026, 11, 19, 12, 0),
//                    ident = "12345678901",
//                    behandlingId = UUID.randomUUID(),
//                    behandletHendelse =
//                        BehandlingOpprettetMeldingV1.BehandletHendelse(
//                            id = UUID.randomUUID(),
//                            type = "Soknad",
//                        ),
//                )
//            val json = KafkaMeldingJson.skriv(melding)
//
//            objectMapper.readTree(json.toJson()).has("migreringId") shouldBe false
//            KafkaMeldingJson.lesBehandlingOpprettet(json) shouldBe melding
//        }

        "skal..." {
            val jsonMessage = JsonMessage(m, MessageProblems(m))
            val melding = KafkaMeldingJson.fra<MigreringSimuleringKontrollResultatMeldingV1>(jsonMessage)

            melding.innhold.verifisert.isNotEmpty() shouldBe true
            melding.packet() shouldNotBe null
        }
    })

private fun ObjectNode.toPacket(): JsonMessage {
    val json = toString()
    return JsonMessage(json, MessageProblems(json))
}

// language=json
private val m =
    """
    {
      "@id": "310fc04c-28c2-49c6-901d-3d099b33ab63",
      "ident": "26418531931",
      "sakId": 13820850,
      "@opprettet": "2026-11-19T23:00:38.073409924",
      "initiertAv": "Z990798",
      "simulering": true,
      "verifisert": [
        {
          "navn": "Prøvingsdato",
          "uuid": "0194881f-91d1-7df2-ba1d-4533f37fcc76",
          "arenaVerdi": "2026-11-19",
          "dpSakVerdi": "2026-11-19",
          "verifisert": true
        },
        {
          "navn": "Fødselsdato",
          "uuid": "0194881f-940b-76ff-acf5-ba7bcb367233",
          "arenaVerdi": "1985-01-26",
          "dpSakVerdi": "1985-01-26",
          "verifisert": true
        },
        {
          "navn": "Aldersgrense",
          "uuid": "0194881f-940b-76ff-acf5-ba7bcb367234",
          "arenaVerdi": 67,
          "dpSakVerdi": 67,
          "verifisert": true
        },
        {
          "navn": "Bruker oppfyller kravet til alder",
          "uuid": "0194881f-940b-76ff-acf5-ba7bcb367237",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Grunnbeløp",
          "uuid": "0194881f-9413-77ce-92ec-d29700f04243",
          "arenaVerdi": 136549,
          "dpSakVerdi": 136549,
          "verifisert": true
        },
        {
          "navn": "Ønsket arbeidstid",
          "uuid": "0194881f-9435-72a8-b1ce-9575cbc2a75f",
          "arenaVerdi": 37.5,
          "dpSakVerdi": 37.5,
          "verifisert": true
        },
        {
          "navn": "Kan jobbe heltid og deltid",
          "uuid": "0194881f-9441-7d1b-a06a-6727543a141e",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Bruker oppfyller kravet til heltid- og deltidsarbeid",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bd8",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Kan jobbe i hele Norge",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bd9",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Bruker oppfyller kravet til mobilitet",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bdb",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Kan ta alle typer arbeid",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bdc",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Oppfyller kravet til å være arbeidsfør",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bdd",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Villig til å bytte yrke",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bde",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Oppfyller kravet til å ta ethvert arbeid",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877bdf",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Registrert som arbeidssøker",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877be0",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Oppfyller kravet til å være registrert som arbeidssøker",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877be1",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Reell arbeidssøker",
          "uuid": "0194881f-9442-707b-a6ee-e96c06877be2",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Har rett til ordinære dagpenger gjennom arbeidsforhold",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d85",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Skal permittering vurderes",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d86",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Forskutterte lønnsgarantimidler i form av dagpenger",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d87",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Skal permittert fra fiskeindustrien vurderes",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d88",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Ordinære dagpenger",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d8a",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Rettighetstype",
          "uuid": "0194881f-9444-7a73-a458-0af81c034d8b",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Avtjent verneplikt",
          "uuid": "01948d3c-4bea-7802-9d18-5342a5e2be99",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Oppfyller kravet til verneplikt",
          "uuid": "01948d43-e218-76f1-b29b-7e604241d98a",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Skal reell arbeidssøker vurderes",
          "uuid": "0194ff86-a035-7eb0-9c60-21899f7cc0c1",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Skal kravet til verneplikt vurderes",
          "uuid": "01980cf4-9010-7bcf-b578-ca5a825d64ef",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Antall dager som er forbrukt",
          "uuid": "01992934-66e4-7606-bdd3-c6c9dd420ffd",
          "arenaVerdi": 49,
          "dpSakVerdi": 49,
          "verifisert": true
        },
        {
          "navn": "Antall dager som gjenstår",
          "uuid": "01992956-e349-76b1-8f68-c9d481df3a32",
          "arenaVerdi": 471,
          "dpSakVerdi": 471,
          "verifisert": true
        },
        {
          "navn": "Mot bedre vitende har gitt uriktige opplysninger",
          "uuid": "019bc5f8-5c56-7f36-bff1-b10c8165764d",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Holder tilbake opplysninger som er viktige for rettigheter eller plikter etter denne loven",
          "uuid": "019bc5f8-8af4-73c7-8b15-6feecd7810d2",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Uten rimelig grunn unnlater å etterkomme pålegg som er gitt med hjemmel i denne loven",
          "uuid": "019bc5f8-c377-740f-9df6-3c743b5144b1",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Har gitt fullstendige og riktige opplysninger",
          "uuid": "019bc5f8-fdf3-7240-afaf-52c0486eddc5",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Oppfyller kravet til framsatt søknad",
          "uuid": "019c092a-7759-700a-9f7d-f4415b91e8db",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Siste forbruksdato",
          "uuid": "019cf5f9-b3ef-7464-8be7-a0f8c3d8f7b2",
          "arenaVerdi": "2026-07-31",
          "dpSakVerdi": "2026-07-31",
          "verifisert": true
        },
        {
          "navn": "Skal kravet til gjenopptak vurderes",
          "uuid": "019d0068-8037-7d91-bfff-de88f9e87fd3",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Vurder rett fra og med",
          "uuid": "019db9de-afaf-7e46-98b4-2fd20655b80f",
          "arenaVerdi": "2026-11-19",
          "dpSakVerdi": "2026-11-19",
          "verifisert": true
        },
        {
          "navn": "Skal eksport vurderes",
          "uuid": "019f608d-f9c5-7ccc-a6f3-c53531d1f00c",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        },
        {
          "navn": "Har gitt alle nødvendige opplysninger?",
          "uuid": "01a01e21-17ce-70c4-aac1-d25c30ecfac9",
          "arenaVerdi": true,
          "dpSakVerdi": true,
          "verifisert": true
        },
        {
          "navn": "Skal etablering vurderes",
          "uuid": "01a0c316-a2e9-7088-acb1-da5596bd0055",
          "arenaVerdi": false,
          "dpSakVerdi": false,
          "verifisert": true
        }
      ],
      "@event_name": "migrering_simulering_kontroll_resultat",
      "migreringId": "01a1f5d3-67a3-701f-aea3-0ca0cec38d7f",
      "ikkeVerifisert": [
        {
          "navn": "Inntektsopplysninger",
          "uuid": "0194881f-9413-77ce-92ec-d29700f04244",
          "arenaVerdi": {
            "inntektsId": "01M523KEMZJNQ6T9Q8AX3M6FDC",
            "inntektsListe": [
              {
                "årMåned": "2024-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 360000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              },
              {
                "årMåned": "2025-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 480000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              },
              {
                "årMåned": "2026-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 480000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              }
            ],
            "hentetTidspunkt": "2026-09-01T12:19:48",
            "manueltRedigert": false,
            "sisteAvsluttendeKalenderMåned": "2026-07"
          },
          "dpSakVerdi": {
            "inntektsId": "01M523KEMZJNQ6T9Q8AX3M6FDC",
            "inntektsListe": [
              {
                "harAvvik": null,
                "årMåned": "2024-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 360000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              },
              {
                "harAvvik": null,
                "årMåned": "2025-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 480000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              },
              {
                "harAvvik": null,
                "årMåned": "2026-07",
                "klassifiserteInntekter": [
                  {
                    "beløp": 480000,
                    "inntektKlasse": "DAGPENGER"
                  }
                ]
              }
            ],
            "manueltRedigert": false,
            "sisteAvsluttendeKalenderMåned": "2026-07"
          },
          "verifisert": false
        },
        {
          "navn": "Brutto arbeidsinntekt",
          "uuid": "0194881f-9413-77ce-92ec-d29700f04245",
          "dpSakVerdi": {
            "inntektsId": "01M523KEMZJNQ6T9Q8AX3M6FDC",
            "inntektsListe": [
              {
                "harAvvik": null,
                "årMåned": "2024-07",
                "klassifiserteInntekter": []
              },
              {
                "harAvvik": null,
                "årMåned": "2025-07",
                "klassifiserteInntekter": []
              },
              {
                "harAvvik": null,
                "årMåned": "2026-07",
                "klassifiserteInntekter": []
              }
            ],
            "manueltRedigert": false,
            "sisteAvsluttendeKalenderMåned": "2026-07"
          },
          "verifisert": false
        },
        {
          "navn": "Oppfyller kravet til minsteinntekt",
          "uuid": "0194881f-9413-77ce-92ec-d29700f0424c",
          "arenaVerdi": true,
          "dpSakVerdi": false,
          "verifisert": false
        },
        {
          "navn": "Minimum vanlig arbeidstid",
          "uuid": "0194881f-9435-72a8-b1ce-9575cbc2a769",
          "arenaVerdi": 0,
          "dpSakVerdi": 18.75,
          "verifisert": false
        },
        {
          "navn": "Har løpende rett på dagpenger",
          "uuid": "01990a09-0eab-7957-b88f-14484a50e194",
          "arenaVerdi": true,
          "dpSakVerdi": false,
          "verifisert": false
        },
        {
          "navn": "Gjenstående egenandel",
          "uuid": "01997b70-a12c-7622-bff8-82a20687e640",
          "arenaVerdi": 0,
          "dpSakVerdi": 0,
          "verifisert": false
        }
      ],
      "@forårsaket_av": {
        "id": "60de8733-2ddd-4c3b-b878-60c4c95ddf04",
        "opprettet": "2026-11-19T23:00:37.568770543",
        "event_name": "migrering_simulering_resultat"
      },
      "system_read_count": 1,
      "migreringTidspunkt": "2026-11-19",
      "system_participating_services": [
        {
          "id": "310fc04c-28c2-49c6-901d-3d099b33ab63",
          "time": "2026-11-19T23:00:38.073409924",
          "image": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-migrering:2026.11.19-21.56-988469d@sha256:a228573b39c0a076dc2638e20ec1dc8c6b28d56a73ac78f09a78be847a951449",
          "service": "dp-migrering",
          "instance": "dp-migrering-748cdd488f-96mdv"
        },
        {
          "id": "310fc04c-28c2-49c6-901d-3d099b33ab63",
          "time": "2026-11-19T23:00:38.090789973",
          "image": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-migrering:2026.11.19-21.56-988469d@sha256:a228573b39c0a076dc2638e20ec1dc8c6b28d56a73ac78f09a78be847a951449",
          "service": "dp-migrering",
          "instance": "dp-migrering-748cdd488f-g5q49"
        }
      ]
    }
    """.trimIndent()
