package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.core.spec.style.StringSpec
import no.nav.dagpenger.migrering.objectMapper

class MigreringMottakSpec :
    StringSpec({

        val rapid = TestRapid()
        MigreringMottak(rapid, objectMapper = objectMapper)

        "skal..." {

            rapid.sendTestMessage(forslagTilBehandlingsresultatMelding)
        }
    })

// language=json
internal val forslagTilBehandlingsresultatMelding =
    """
    {
      "@id": "a44bc5ab-bb65-4729-b3fa-89cbaa7cd20d",
      "ident": "04518746699",
      "førteTil": "Innvilgelse",
      "opprettet": "2026-10-29T09:27:10.740029941",
      "regelverk": "Dagpenger",
      "@opprettet": "2026-10-29T09:27:11.024685659",
      "automatisk": true,
      "sistEndret": "2026-10-29T09:27:10.831343414",
      "@event_name": "forslag_til_behandlingsresultat",
      "behandletAv": [],
      "behandlingId": "01a186c5-2a13-70f5-9755-cb15744f843e",
      "opplysninger": [
        {
          "navn": "hendelseType",
          "datatype": "tekst",
          "perioder": [
            {
              "id": "01a186c5-2a15-75d4-b359-c4c1a3086979",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.741621621"
              },
              "verdi": {
                "verdi": "MigrerYtelseHendelse",
                "datatype": "tekst"
              },
              "opprettet": "2026-10-29T09:27:10.741654041",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29",
              "gyldigTilOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "01958ef2-e237-77c4-89e1-de91256e2e4a"
        },
        {
          "navn": "Oppfyller vilkårene til etablering av egen virksomhet",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f554",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744539391",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "01a0d282-3d10-772b-83fa-7ce6660e7d8b"
        },
        {
          "navn": "Oppfyller vilkåret om å ikke motta andre fulle ytelser",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f556",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744559791",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-943f-78d9-b874-00a4944c54f1"
        },
        {
          "navn": "Oppfyller kravet for gjenopptak av stønadsperiode",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f557",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744562931",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019a2fda-4ad6-7711-89ec-38dec1b86293"
        },
        {
          "navn": "Har gitt alle nødvendige opplysninger?",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f558",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744565351",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "01a01e21-17ce-70c4-aac1-d25c30ecfac9"
        },
        {
          "navn": "Oppfyller meldeplikt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f559",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744567691",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019d870c-6847-71fa-89d2-81a53d19cfcf"
        },
        {
          "navn": "Oppfyller kravet til minsteinntekt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f55a",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74457002",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9413-77ce-92ec-d29700f0424c"
        },
        {
          "navn": "Oppfyller kravet til opphold i Norge eller unntak",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f55b",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744576111",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9443-72b4-8b30-5f6cdb24d54b"
        },
        {
          "navn": "Oppfyller kravet til medlemskap",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f55c",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74458412",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9443-72b4-8b30-5f6cdb24d54d"
        },
        {
          "navn": "Oppfyller kravet til permittering",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f55e",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744588821",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194d111-db2f-7395-bcfb-959f245fd2a6"
        },
        {
          "navn": "Oppfyller kravet til permittering i fiskeindustrien",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f55f",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74459111",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019522b0-c722-76d4-8d7f-78f556c51f72"
        },
        {
          "navn": "Oppfyller kravet til å være arbeidsfør",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f562",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74461095",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877bdd"
        },
        {
          "navn": "Oppfyller kravet til å ta ethvert arbeid",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f563",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744622181",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877bdf"
        },
        {
          "navn": "Reell arbeidssøker",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f564",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744628981",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877be2"
        },
        {
          "navn": "Er ilagt sanskjonsperiode ved selvforskyldt arbeidsløshet",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f567",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744647601",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019e82fc-04e7-7f8d-bd52-6a7041ebb15f"
        },
        {
          "navn": "Brukeren er ikke påvirket av streik eller lock-out",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f568",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74464995",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-91df-746a-a8ac-4a6b2b30685f"
        },
        {
          "navn": "Oppfyller kravet til framsatt søknad",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f569",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74465453",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019c092a-7759-700a-9f7d-f4415b91e8db"
        },
        {
          "navn": "Oppfyller vilkåret til tap av arbeidsinntekt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56a",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74465771",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9435-72a8-b1ce-9575cbc2a761"
        },
        {
          "navn": "Oppfyller vilkåret om tap av arbeidstid",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56b",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744663201",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9435-72a8-b1ce-9575cbc2a76e"
        },
        {
          "navn": "Oppfyller vilkåret om tap av arbeidsinntekt og arbeidstid",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56c",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.744665881",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9435-72a8-b1ce-9575cbc2a76f"
        },
        {
          "navn": "Er ilagt tidsbegrenset bortfall av dagpenger",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56d",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74467187",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019df9b1-7052-72e9-9232-547f7cb6ba60"
        },
        {
          "navn": "Oppfyller kravene til tre meldeperioder uten tilstrekkelig tap av arbeidstid",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56e",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74467387",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019f45da-6f59-7155-bda1-e219a5f3dd89"
        },
        {
          "navn": "Har gitt fullstendige og riktige opplysninger",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f56f",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74468036",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019bc5f8-fdf3-7240-afaf-52c0486eddc5"
        },
        {
          "navn": "Oppfyller krav til utdanning eller opplæring",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f570",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74468581",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9445-734c-a7ee-045edf29b52d"
        },
        {
          "navn": "Oppfyller krav til ikke utestengt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f571",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74469191",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9447-7e36-a569-3e9f42bff9f7"
        },
        {
          "navn": "Prøvingsdato",
          "datatype": "dato",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f573",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": "2026-10-29",
                "datatype": "dato"
              },
              "opprettet": "2026-10-29T09:27:10.74469958",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-91d1-7df2-ba1d-4533f37fcc76"
        },
        {
          "navn": "fagsakId",
          "datatype": "heltall",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f574",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 13820808,
                "datatype": "heltall"
              },
              "opprettet": "2026-10-29T09:27:10.74470576",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9462-78af-8977-46092bb030eb"
        },
        {
          "navn": "Oppfyller vilkåret om eksport av dagpenger til EØS-land",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f575",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": false,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74470832",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019f6094-681b-7233-9558-da8bf7527cd3"
        },
        {
          "navn": "Barn som gir tillegg",
          "datatype": "barn",
          "perioder": [
            {
              "id": "01a186c5-2a18-77c1-b1f0-9e4ee687f576",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": [],
                "datatype": "barn"
              },
              "opprettet": "2026-10-29T09:27:10.74471",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "019e2085-a97a-776b-ba2c-4bca8e01a166"
        },
        {
          "navn": "Bruker er medlem av folketrygden",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef2b",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.7481767",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9443-72b4-8b30-5f6cdb24d54c"
        },
        {
          "navn": "Kravet til opphold i Norge er oppfylt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef2c",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74820993",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9443-72b4-8b30-5f6cdb24d54e"
        },
        {
          "navn": "Dagpengegrunnlag",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef2d",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 743561,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74827089",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9410-7481-b263-4606fdd10cbd"
        },
        {
          "navn": "Siste avsluttende kalendermåned",
          "datatype": "dato",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef2e",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": "2026-08-31",
                "datatype": "dato"
              },
              "opprettet": "2026-10-29T09:27:10.74828894",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9414-7823-8d29-0e25b7feb7d0"
        },
        {
          "navn": "Oppfyller kravet til heltid- og deltidsarbeid",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef2f",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74829579",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877bd8"
        },
        {
          "navn": "Oppfyller kravet til mobilitet",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef30",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74838163",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877bdb"
        },
        {
          "navn": "Ukessats med barnetillegg etter samordning",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef31",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 8925,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74848498",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9428-74d5-b160-f63a4c61a24e"
        },
        {
          "navn": "Avrundet ukessats med barnetillegg før samordning",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef32",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 8925,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74851306",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9428-74d5-b160-f63a4c61a240"
        },
        {
          "navn": "Har samordnet",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef33",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": false,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74853398",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9428-74d5-b160-f63a4c61a250"
        },
        {
          "navn": "Utbetalt inntekt periode 1",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef34",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 720000,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74854855",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9410-7481-b263-4606fdd10cad"
        },
        {
          "navn": "Utbetalt inntekt periode 2",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef35",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 600000,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74856117",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9410-7481-b263-4606fdd10cae"
        },
        {
          "navn": "Utbetalt inntekt periode 3",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef36",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 480000,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74857097",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9410-7481-b263-4606fdd10caf"
        },
        {
          "navn": "Oppfyller kravet til alder",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef37",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74857539",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-940b-76ff-acf5-ba7bcb367237"
        },
        {
          "navn": "Dagsats uten barnetillegg samordnet",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef38",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 1785,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74861015",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-942f-7bde-ab16-68ffd19e9a33"
        },
        {
          "navn": "Oppfyller kravet til å være registrert som arbeidssøker",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef39",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74862587",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9442-707b-a6ee-e96c06877be1"
        },
        {
          "navn": "Fødselsdato",
          "datatype": "dato",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3a",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": "1987-11-04",
                "datatype": "dato"
              },
              "opprettet": "2026-10-29T09:27:10.74865904",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-940b-76ff-acf5-ba7bcb367233"
        },
        {
          "navn": "Antall stønadsuker (stønadsperiode)",
          "datatype": "heltall",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3b",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "enhet": "uker",
                "verdi": 104,
                "datatype": "heltall"
              },
              "opprettet": "2026-10-29T09:27:10.7486664",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-943d-77a7-969c-147999f15459"
        },
        {
          "navn": "Antall stønadsdager",
          "datatype": "heltall",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3c",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "enhet": "dager",
                "verdi": 520,
                "datatype": "heltall"
              },
              "opprettet": "2026-10-29T09:27:10.74867659",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-943d-77a7-969c-147999f15457"
        },
        {
          "navn": "Antall barn som gir rett til barnetillegg",
          "datatype": "heltall",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3d",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 0,
                "datatype": "heltall"
              },
              "opprettet": "2026-10-29T09:27:10.74869311",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9428-74d5-b160-f63a4c61a23c"
        },
        {
          "navn": "Fastsatt arbeidstid per uke før tap",
          "datatype": "desimaltall",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3e",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "enhet": "timer",
                "verdi": 40.0,
                "datatype": "desimaltall"
              },
              "opprettet": "2026-10-29T09:27:10.74870638",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9435-72a8-b1ce-9575cbc2a76a"
        },
        {
          "navn": "Egenandel",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef3f",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 5355,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74871934",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-943f-78d9-b874-00a4944c54ef"
        },
        {
          "navn": "Krav til prosentvis tap av arbeidstid",
          "datatype": "desimaltall",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef40",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "enhet": "prosent",
                "verdi": 50.0,
                "datatype": "desimaltall"
              },
              "opprettet": "2026-10-29T09:27:10.7487626",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9435-72a8-b1ce-9575cbc2a762"
        },
        {
          "navn": "Samordnet dagsats uten barnetillegg",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef41",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 1785,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74880486",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9434-79e8-a64d-1a23cc5d86eb"
        },
        {
          "navn": "Medlem har reduserte ytelser fra folketrygden (Samordning)",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef42",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": false,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.7488196",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9434-79e8-a64d-1a23cc5d86ea"
        },
        {
          "navn": "Utfall etter samordning",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef43",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.7488333",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9434-79e8-a64d-1a23cc5d86ef"
        },
        {
          "navn": "Oppgitt andre ytelser utenfor NAV i søknaden",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef44",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": false,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74892424",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-942e-7cb0-aa59-05ea449d88e0"
        },
        {
          "navn": "Dagsats med barnetillegg etter samordning og 90 % regel",
          "datatype": "penger",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef45",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": 1785,
                "datatype": "penger"
              },
              "opprettet": "2026-10-29T09:27:10.74893845",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "0194881f-9428-74d5-b160-f63a4c61a24f"
        },
        {
          "navn": "Ordinære dagpenger",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef46",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74895886",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "0194881f-9444-7a73-a458-0af81c034d8a"
        },
        {
          "navn": "Oppfyller kravet til verneplikt",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a1c-713f-855d-995e3be3ef47",
              "type": "Faktum",
              "kilde": {
                "type": "System",
                "meldingId": "4ac59b14-570b-45ea-9243-fc905d9e0028",
                "registrert": "2026-10-29T09:27:10.62421498"
              },
              "verdi": {
                "verdi": false,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.74896486",
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-15"
            }
          ],
          "opplysningTypeId": "01948d43-e218-76f1-b29b-7e604241d98a"
        },
        {
          "navn": "Har løpende rett på dagpenger",
          "datatype": "boolsk",
          "perioder": [
            {
              "id": "01a186c5-2a6d-7100-8f7d-534f3b910abc",
              "type": "Faktum",
              "verdi": {
                "verdi": true,
                "datatype": "boolsk"
              },
              "opprettet": "2026-10-29T09:27:10.829794504",
              "utledetAv": {
                "regel": {
                  "navn": "RettighetsperiodePlugin"
                },
                "versjon": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-behandling:2026.10.29-08.15-ed4d52a@sha256:6093f686e11bab209fdb5d969b4aab2ecf37bbb020e495d5a2d5b5511d995c7e",
                "opplysninger": [
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f556",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f557",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f558",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f55a",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f564",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f567",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f568",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f569",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f56c",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f56d",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f56f",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f570",
                  "01a186c5-2a18-77c1-b1f0-9e4ee687f571",
                  "01a186c5-2a1c-713f-855d-995e3be3ef2c",
                  "01a186c5-2a1c-713f-855d-995e3be3ef37",
                  "01a186c5-2a1c-713f-855d-995e3be3ef39"
                ]
              },
              "opprinnelse": "Ny",
              "gyldigFraOgMed": "2026-10-29"
            }
          ],
          "opplysningTypeId": "01990a09-0eab-7957-b88f-14484a50e194"
        }
      ],
      "utbetalinger": [],
      "behandletHendelse": {
        "id": "01a186c4-abb0-7099-92e6-d9efba3b7d0e",
        "type": "Migrering",
        "skjedde": "2026-10-29",
        "datatype": "UUID"
      },
      "system_read_count": 0,
      "behandlingskjedeId": "01a186c5-2a13-70f5-9755-cb15744f843e",
      "rettighetsperioder": [
        {
          "harRett": true,
          "fraOgMed": "2026-10-29",
          "opprinnelse": "Ny"
        }
      ],
      "system_participating_services": [
        {
          "id": "a44bc5ab-bb65-4729-b3fa-89cbaa7cd20d",
          "time": "2026-10-29T09:27:11.024685659",
          "image": "europe-north1-docker.pkg.dev/nais-management-233d/teamdagpenger/dp-behandling:2026.10.29-08.15-ed4d52a@sha256:6093f686e11bab209fdb5d969b4aab2ecf37bbb020e495d5a2d5b5511d995c7e",
          "service": "dp-behandling",
          "instance": "dp-behandling-5789bf5744-k86c6"
        }
      ]
    }
    """.trimIndent()
