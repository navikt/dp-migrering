# Migreringsflyt

Meldingsflyten kjøres med TestRapid og den faktiske MigreringMediator.
Den andre tjenesten simuleres, og repository-et mockes.
Dokumentet viser hele JSON-meldingen i hvert steg, inkludert transportmetadata.
Variable tekstverdier i transportmetadata er erstattet med `<metadata>`.
Den genererte migrerings-ID-en er erstattet med `<migreringId>`.

## 1. Migreringen starter uten opplysninger

Den første migrering_startet-meldingen har en tom opplysningsliste. Mottaket svarer ikke.

```json
{
  "@event_name" : "migrering_startet",
  "@id" : "<metadata>",
  "@opprettet" : "<metadata>",
  "ident" : "testident",
  "initiertAv" : "testbruker",
  "migreringId" : "<migreringId>",
  "migreringsTidspunkt" : "2026-11-06",
  "opplysninger" : [ ],
  "sakId" : 12345,
  "simulering" : true,
  "system_participating_services" : [
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    }
  ],
  "system_read_count" : 0
}
```

## 2. Den andre tjenesten etterspør dagsats

Tjenesten setter manglendeOpplysninger på startmeldingen og beholder den tomme opplysningslisten.

```json
{
  "@event_name" : "migrering_startet",
  "@id" : "<metadata>",
  "@opprettet" : "<metadata>",
  "ident" : "testident",
  "initiertAv" : "testbruker",
  "manglendeOpplysninger" : [
    "0194881f-9428-74d5-b160-f63a4c61a24f"
  ],
  "migreringId" : "<migreringId>",
  "migreringsTidspunkt" : "2026-11-06",
  "opplysninger" : [ ],
  "sakId" : 12345,
  "simulering" : true,
  "system_participating_services" : [
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    }
  ],
  "system_read_count" : 0
}
```

## 3. Mottaket sender dagsats

Mottaket henter dagsats fra repository-et, publiserer den og markerer den som migrert.

```json
{
  "@event_name" : "migrering_startet",
  "@id" : "<metadata>",
  "@opprettet" : "<metadata>",
  "ident" : "testident",
  "initiertAv" : "testbruker",
  "manglendeOpplysninger" : null,
  "migreringId" : "<migreringId>",
  "migreringsTidspunkt" : "2026-11-06",
  "opplysninger" : [
    {
      "gyldigFraOgMed" : "2026-11-06",
      "navn" : "Dagsats",
      "uuid" : "0194881f-9428-74d5-b160-f63a4c61a24f",
      "verdi" : 1500
    }
  ],
  "sakId" : 12345,
  "simulering" : true,
  "system_participating_services" : [
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    },
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    }
  ],
  "system_read_count" : 1
}
```

## 4. Den andre tjenesten etterspør samordning

Svaret inneholder dagsatsen tjenesten allerede har mottatt, og etterspør samordning.

```json
{
  "@event_name" : "migrering_startet",
  "@id" : "<metadata>",
  "@opprettet" : "<metadata>",
  "ident" : "testident",
  "initiertAv" : "testbruker",
  "manglendeOpplysninger" : [
    "0194881f-9428-74d5-b160-f63a4c61a23c"
  ],
  "migreringId" : "<migreringId>",
  "migreringsTidspunkt" : "2026-11-06",
  "opplysninger" : [
    {
      "gyldigFraOgMed" : "2026-11-06",
      "navn" : "Dagsats",
      "uuid" : "0194881f-9428-74d5-b160-f63a4c61a24f",
      "verdi" : 1500
    }
  ],
  "sakId" : 12345,
  "simulering" : true,
  "system_participating_services" : [
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    },
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    }
  ],
  "system_read_count" : 1
}
```

## 5. Mottaket sender tidligere migrerte og nye opplysninger

Repository-et returnerer både tidligere migrert dagsats og etterspurt samordning. Mottaket publiserer begge uten duplikater og markerer dem som migrert.

```json
{
  "@event_name" : "migrering_startet",
  "@id" : "<metadata>",
  "@opprettet" : "<metadata>",
  "ident" : "testident",
  "initiertAv" : "testbruker",
  "manglendeOpplysninger" : null,
  "migreringId" : "<migreringId>",
  "migreringsTidspunkt" : "2026-11-06",
  "opplysninger" : [
    {
      "gyldigFraOgMed" : "2026-11-06",
      "navn" : "Dagsats",
      "uuid" : "0194881f-9428-74d5-b160-f63a4c61a24f",
      "verdi" : 1500
    },
    {
      "gyldigFraOgMed" : null,
      "navn" : "Samordnet",
      "uuid" : "0194881f-9428-74d5-b160-f63a4c61a23c",
      "verdi" : false
    }
  ],
  "sakId" : 12345,
  "simulering" : true,
  "system_participating_services" : [
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    },
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    },
    {
      "id" : "<metadata>",
      "time" : "<metadata>"
    }
  ],
  "system_read_count" : 2
}
```

