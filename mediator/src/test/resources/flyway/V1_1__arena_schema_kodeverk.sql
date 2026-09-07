--------------------------------------------------------
--  DDL for Table AKTIVITETFASE
--------------------------------------------------------
CREATE TABLE "AKTIVITETFASE"
(
    "AKTFASEKODE" VARCHAR2(10)         NOT NULL,
    "AKTFASENAVN" VARCHAR2(35)         NOT NULL,
    "DATO_FRA"    DATE DEFAULT CURRENT_DATE NOT NULL,
    "DATO_TIL"    DATE                 NOT NULL,
    "REG_DATO"    DATE,
    "MOD_DATO"    DATE,
    "REG_USER"    VARCHAR2(8),
    "MOD_USER"    VARCHAR2(8),
    CONSTRAINT "AKTFAS_PK" PRIMARY KEY ("AKTFASEKODE")
);

COMMENT ON COLUMN "AKTIVITETFASE"."AKTFASEKODE" IS 'Entydig kode på aktivitetsfasen';
COMMENT ON COLUMN "AKTIVITETFASE"."AKTFASENAVN" IS 'Navnet på aktivitestfasekoden.';
COMMENT ON COLUMN "AKTIVITETFASE"."DATO_FRA" IS 'Fra-dato for gyldighetsperioden for koden';
COMMENT ON COLUMN "AKTIVITETFASE"."DATO_TIL" IS 'Til-dato for gyldighetsperioden for koden';
COMMENT ON TABLE "AKTIVITETFASE" IS 'Beskriver faser i et rettighetsløp';

--------------------------------------------------------
--  DDL for Table ANMERKNINGTYPE
--------------------------------------------------------

CREATE TABLE "ANMERKNINGTYPE"
(	"ANMERKNINGKODE" VARCHAR2(5),
     "ANMERKNINGNAVN" VARCHAR2(100),
     "BESKRIVELSE" VARCHAR2(255),
     "HENDELSETYPEKODE" VARCHAR2(7),
     CONSTRAINT "ANMTYP_PK" PRIMARY KEY ("ANMERKNINGKODE")
);

COMMENT ON COLUMN "ANMERKNINGTYPE"."ANMERKNINGKODE" IS 'Entydig kode for anmerkningtypen';
COMMENT ON COLUMN "ANMERKNINGTYPE"."BESKRIVELSE" IS 'Beskrivelse av anmerkningtypen (kan inkludere flettefelt for verdien fra ANMERKNING)';
COMMENT ON COLUMN "ANMERKNINGTYPE"."HENDELSETYPEKODE" IS 'Referanse til HENDELSETYPE';
COMMENT ON TABLE "ANMERKNINGTYPE"  IS 'Kodetabell for anmerkningtyper';
COMMENT ON COLUMN "ANMERKNINGTYPE"."ANMERKNINGNAVN" IS 'Navn på anmerkningtypen';

--------------------------------------------------------
--  DDL for Table ATTRIBUTTYPE
--------------------------------------------------------

CREATE TABLE "ATTRIBUTTYPE"
(	"SKJERMBILDETEKST" VARCHAR2(255),
     "STATUS_OBLIG" VARCHAR2(1),
     "BESKRIVELSE" VARCHAR2(255),
     "ATTRIBUTTYPE_ID" NUMBER,
     "SAKSOPPLYSNINGKODE" VARCHAR2(10),
     "STATUS_REPETERBAR" VARCHAR2(1),
     "FORMATNAVN" VARCHAR2(255) DEFAULT NULL,
     "ATTRIBUTTYPE_ID_OVERORDNET" NUMBER,
     "FELTLENGDE" NUMBER(4,0),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "POSISJON" NUMBER(2,0),
     "STATUS_TITTEL" VARCHAR2(1),
     "ATTRIBUTTKODE" VARCHAR2(5),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "LOVKODE" VARCHAR2(20),
     "MAKS_VERDI" VARCHAR2(30),
     "MIN_VERDI" VARCHAR2(30),
     CONSTRAINT "ATTRTYP_PK" PRIMARY KEY ("ATTRIBUTTYPE_ID")
);

COMMENT ON COLUMN "ATTRIBUTTYPE"."SKJERMBILDETEKST" IS 'Teksten for attributtet som vises i skjermbildet';
COMMENT ON COLUMN "ATTRIBUTTYPE"."STATUS_OBLIG" IS 'Angir om attributtet er obligatorisk å fylle ut eller ikke';
COMMENT ON COLUMN "ATTRIBUTTYPE"."BESKRIVELSE" IS 'Beskriver attributtet (lite brukt og da ren kopi av skermbildetekst)';
COMMENT ON COLUMN "ATTRIBUTTYPE"."ATTRIBUTTYPE_ID" IS 'Unik ID';
COMMENT ON COLUMN "ATTRIBUTTYPE"."SAKSOPPLYSNINGKODE" IS 'Relatert saksopplysningstype';
COMMENT ON COLUMN "ATTRIBUTTYPE"."STATUS_REPETERBAR" IS 'Angir om man kan ha flere av samme attributtype for samme sak.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."FORMATNAVN" IS 'Angir formatet på attributtet';
COMMENT ON COLUMN "ATTRIBUTTYPE"."ATTRIBUTTYPE_ID_OVERORDNET" IS 'Referanse til den overordnede attributtypen.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."FELTLENGDE" IS 'Angir evt. maks feltlengde for attributtypen';
COMMENT ON COLUMN "ATTRIBUTTYPE"."DATO_FRA" IS 'Angir fra-dato for evt. gyldihetsperiode for attributtypen.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."DATO_TIL" IS 'Angir til-dato for evt. gyldihetsperiode for attributtypen.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."POSISJON" IS 'Angir posisjonen i rekkefølgen som atributtypene skal vises i i saksopplysningsskjermbildet.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."STATUS_TITTEL" IS 'Angir om attributtypen er master i attributthierarkiet og da skal vises i master-delen av skjermbildet.';
COMMENT ON COLUMN "ATTRIBUTTYPE"."ATTRIBUTTKODE" IS 'Entydig attributtkode';
COMMENT ON COLUMN "ATTRIBUTTYPE"."LOVKODE" IS 'Kode som kan knytte opp en Forms verdiliste definisjon fra DYNAMISK_VERDILISTE';
COMMENT ON COLUMN "ATTRIBUTTYPE"."MAKS_VERDI" IS 'Maksimumsverdi for aktuell attributttype';
COMMENT ON COLUMN "ATTRIBUTTYPE"."MIN_VERDI" IS 'Miniimumsverdi for aktuell attributttype';
COMMENT ON TABLE "ATTRIBUTTYPE"  IS 'Definerer egenskaper ved en attributtype';


--------------------------------------------------------
--  DDL for Table BEREGNINGSLEDDTYPE
--------------------------------------------------------

CREATE TABLE "BEREGNINGSLEDDTYPE"
(	"BEREGNINGSLEDDKODE" VARCHAR2(5),
     "BEREGNINGSLEDDNAVN" VARCHAR2(60),
     "STATUS_KRAV_NY_BEREGNING" VARCHAR2(1),
     "STATUS_KVOTEBRUK" VARCHAR2(1),
     "STATUS_SETT_TILDATO" VARCHAR2(1),
     "DATO_GYLDIG_FRA" DATE,
     "DATO_GYLDIG_TIL" DATE,
     "BESKRIVELSE" VARCHAR2(255),
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "VALIDER_HELE_DAGER" VARCHAR2(1) DEFAULT 'N' NOT NULL CHECK (VALIDER_HELE_DAGER IN ('J', 'N')),
     CONSTRAINT "BERLDTYP_PK" PRIMARY KEY ("BEREGNINGSLEDDKODE")
);

COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."BEREGNINGSLEDDKODE" IS 'Entydig kode';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."BEREGNINGSLEDDNAVN" IS 'Navnet på beregningsleddtypen';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."STATUS_KRAV_NY_BEREGNING" IS '(Ikke i bruk)';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."STATUS_KVOTEBRUK" IS 'Angir om beregningsleddtypen representerer en teller (skal da kun kunne oppdateres av kvotebruk)';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."STATUS_SETT_TILDATO" IS '(Ikke i bruk)';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."DATO_GYLDIG_FRA" IS 'Fra-dato for beregningsleddtypens gyldighetsperiode';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."DATO_GYLDIG_TIL" IS 'Til-dato for beregningsleddtypens gyldighetsperiode';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."BESKRIVELSE" IS 'Beskrivelse av beregningsleddtypen';
COMMENT ON COLUMN "BEREGNINGSLEDDTYPE"."VALIDER_HELE_DAGER" IS 'Hvilke tellere som skal valideres for at gitt verdi kun skal kunne gis for hele dager, dvs. at verdien er delelig med 20';
COMMENT ON TABLE "BEREGNINGSLEDDTYPE"  IS 'Bestemmer egenskaper for beregningsledd.';


--------------------------------------------------------
--  DDL for Table BEREGNINGSTATUS
--------------------------------------------------------

CREATE TABLE "BEREGNINGSTATUS"
(	"BEREGNINGSTATUSKODE" VARCHAR2(5),
     "BEREGNINGSTATUSNAVN" VARCHAR2(30),
     CONSTRAINT "BERSTAT_PK" PRIMARY KEY ("BEREGNINGSTATUSKODE")
)  ;

COMMENT ON COLUMN "BEREGNINGSTATUS"."BEREGNINGSTATUSKODE" IS 'Entydig kode';
COMMENT ON COLUMN "BEREGNINGSTATUS"."BEREGNINGSTATUSNAVN" IS 'Navn på beregningsstatusen';
COMMENT ON TABLE "BEREGNINGSTATUS"  IS 'Beregningsstatuser';


--------------------------------------------------------
--  DDL for Table FORMATVERDI
--------------------------------------------------------

CREATE TABLE "FORMATVERDI"
(	"FORMATNAVN" VARCHAR2(255),
     "VERDIKODE" VARCHAR2(100),
     "VERDINAVN" VARCHAR2(255),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "INNTEKTKLASSEKODE" VARCHAR2(1),
     "TILLEGGSINFOTYPE" VARCHAR2(50),
     "TILLEGGSINFOVERDI" VARCHAR2(100),
     "GYLDIG_DATO_TIL" DATE,
     "GYLDIG_DATO_FRA" DATE,
     CONSTRAINT "FMTVERD_PK" PRIMARY KEY ("FORMATNAVN", "VERDIKODE")
) ;

COMMENT ON COLUMN "FORMATVERDI"."FORMATNAVN" IS 'Referanse til FORMAT, navn på verdiliste';
COMMENT ON COLUMN "FORMATVERDI"."VERDIKODE" IS 'Selve verdien i brukt i verdilista';
COMMENT ON COLUMN "FORMATVERDI"."VERDINAVN" IS 'Beskrivende navn på rad i verdilista';
COMMENT ON COLUMN "FORMATVERDI"."DATO_FRA" IS 'Tidligste dato verdien skal kunne velges / vil vises i verdiliste (benytter dagens dato)';
COMMENT ON COLUMN "FORMATVERDI"."DATO_TIL" IS 'Siste dato verdien skal kunne velges / vil vises i verdiliste (benytter dagens dato)';
COMMENT ON COLUMN "FORMATVERDI"."INNTEKTKLASSEKODE" IS 'Intektsklasse, bare for formatnavn Inntektskode';
COMMENT ON COLUMN "FORMATVERDI"."GYLDIG_DATO_TIL" IS 'Siste dato verdien skal kunne benyttes i et vedtak (vedtakets fra-dato)';
COMMENT ON COLUMN "FORMATVERDI"."GYLDIG_DATO_FRA" IS 'Tidligste dato verdien skal kunne benyttes i et vedtak (vedtakets fra-dato)';
COMMENT ON TABLE "FORMATVERDI"  IS 'Inneholder alle verdier i verdilister for saksopplysninger';


--------------------------------------------------------
--  DDL for Table HENDELSETYPE
--------------------------------------------------------

CREATE TABLE "HENDELSETYPE"
(	"HENDELSETYPEKODE" VARCHAR2(7),
     "HENDELSETYPENAVN" VARCHAR2(50),
     "METODEREFERANSE_LOGG" VARCHAR2(255),
     "TABELLNAVNALIAS_ORIGINALKILDE" VARCHAR2(10),
     "BESKRIVELSE" VARCHAR2(255),
     "HENDELSEGRUPPEKODE" VARCHAR2(5),
     CONSTRAINT "HENDTYP_PK" PRIMARY KEY ("HENDELSETYPEKODE")
) ;

COMMENT ON COLUMN "HENDELSETYPE"."HENDELSETYPEKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "HENDELSETYPE"."HENDELSETYPENAVN" IS 'Hendelsetypenavn';
COMMENT ON COLUMN "HENDELSETYPE"."METODEREFERANSE_LOGG" IS 'Generell tekst som brukes i logg';
COMMENT ON COLUMN "HENDELSETYPE"."TABELLNAVNALIAS_ORIGINALKILDE" IS 'Referanse til OBJEKTTYPE';
COMMENT ON COLUMN "HENDELSETYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON COLUMN "HENDELSETYPE"."HENDELSEGRUPPEKODE" IS 'Referanse til HENDELSEGRUPPE';
COMMENT ON TABLE "HENDELSETYPE"  IS 'Hendelser som er definert i systemet. Brukes for å styre hva som skal med i logglinjene.';


--------------------------------------------------------
--  DDL for Table KVOTETYPE
--------------------------------------------------------

CREATE TABLE "KVOTETYPE"
(	"KVOTETYPEKODE" VARCHAR2(5),
     "KVOTETYPENAVN" VARCHAR2(60),
     "MAALEENHET" VARCHAR2(5),
     "BEREGNINGSLEDDKODE" VARCHAR2(5),
     CONSTRAINT "KVOTTYP_PK" PRIMARY KEY ("KVOTETYPEKODE")
)  ;

COMMENT ON COLUMN "KVOTETYPE"."KVOTETYPEKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "KVOTETYPE"."KVOTETYPENAVN" IS 'Kvotetypenavn';
COMMENT ON COLUMN "KVOTETYPE"."MAALEENHET" IS 'Referanse til MAALEENHETVERDITYPE';
COMMENT ON COLUMN "KVOTETYPE"."BEREGNINGSLEDDKODE" IS 'Referanse til BEREGNINGSLEDDTYPE';
COMMENT ON TABLE "KVOTETYPE"  IS 'Type teller brukt i KVOTEBRUK';


--------------------------------------------------------
--  DDL for Table MELDEGRUPPETYPE
--------------------------------------------------------

CREATE TABLE "MELDEGRUPPETYPE"
(	"MELDEGRUPPEKODE" VARCHAR2(5),
     "MELDEGRUPPENAVN" VARCHAR2(80),
     "NIVAA" NUMBER(1,0),
     "STATUS_VERDILISTE" VARCHAR2(1),
     CONSTRAINT "MGRPTYP_PK" PRIMARY KEY ("MELDEGRUPPEKODE")
)  ;

COMMENT ON COLUMN "MELDEGRUPPETYPE"."MELDEGRUPPEKODE" IS 'Entydlig kode som identifiserer en meldegruppetype';
COMMENT ON COLUMN "MELDEGRUPPETYPE"."MELDEGRUPPENAVN" IS 'Navn på meldegruppetypen';
COMMENT ON COLUMN "MELDEGRUPPETYPE"."NIVAA" IS 'Definerer rang for meldegruppetyper. Meldegruppe av høyere rang skal overstyre meldegruppe av lavere rang';
COMMENT ON COLUMN "MELDEGRUPPETYPE"."STATUS_VERDILISTE" IS 'Definerer om meldegruppetypen skal vises i lister for valg av meldegruppetype';
COMMENT ON TABLE "MELDEGRUPPETYPE"  IS 'Liste over lovlige meldegruppetyper. Meldegruppe av høyere rang skal overstyre meldegruppe av lavere rang, dette defineres i kolonnen NIVAA';

--------------------------------------------------------
--  DDL for Table MKSKORTTYPE
--------------------------------------------------------

CREATE TABLE "MKSKORTTYPE"
(	"MKSKORTKODE" VARCHAR2(2),
     "MKSKORTTYPENAVN" VARCHAR2(30),
     CONSTRAINT "MKSKORTTYP_PK" PRIMARY KEY ("MKSKORTKODE")
)  ;

COMMENT ON COLUMN "MKSKORTTYPE"."MKSKORTKODE" IS 'Entydlig kode som identifiserer en mkskorttype';
COMMENT ON COLUMN "MKSKORTTYPE"."MKSKORTTYPENAVN" IS 'Navn på meldekorttype';
COMMENT ON TABLE "MKSKORTTYPE"  IS 'Lovlige meldekorttyper (elektronisk, papir, manuelt osv)';


--------------------------------------------------------
--  DDL for Table RETTIGHETTYPE
--------------------------------------------------------

CREATE TABLE "RETTIGHETTYPE"
(	"RETTIGHETKODE" VARCHAR2(10),
     "RETTIGHETNAVN" VARCHAR2(60),
     "DATO_GYLDIG_FRA" DATE,
     "DATO_GYLDIG_TIL" DATE,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "SAKSKODE" VARCHAR2(10),
     "RETTIGHETSKLASSEKODE" VARCHAR2(5),
     "BELOPKODE" VARCHAR2(5),
     "RANGNR" NUMBER(3,0),
     "TRANSAKSJONSKODE" VARCHAR2(5),
     "STATUS_KONTERBAR" VARCHAR2(1) DEFAULT 'N',
     "TRANSAKSJONSKODE_FORSKUDD" VARCHAR2(5),
     "RETTIGHETNAVN_KORT" VARCHAR2(20),
     "FORSKUDD_BETPLAN" VARCHAR2(1),
     "SATSVALG" VARCHAR2(10),
     "STATUS_TILTAK" VARCHAR2(1),
     "STATUS_START_VEDTAK" VARCHAR2(1),
     "BILAG_KREVES_JN" VARCHAR2(1),
     "BETPLAN_JN" VARCHAR2(1) DEFAULT 'N',
     "GJELDERKODE" VARCHAR2(10),
     CONSTRAINT "RETTYP_PK" PRIMARY KEY ("RETTIGHETKODE")
) ;

COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETNAVN" IS 'Rettighetnavn';
COMMENT ON COLUMN "RETTIGHETTYPE"."DATO_GYLDIG_FRA" IS 'Dato gyldig fra';
COMMENT ON COLUMN "RETTIGHETTYPE"."DATO_GYLDIG_TIL" IS 'Dato gyldig til';
COMMENT ON COLUMN "RETTIGHETTYPE"."SAKSKODE" IS 'Referanse til SAK';
COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETSKLASSEKODE" IS 'Referanse til RETTIGHETSKLASSE';
COMMENT ON COLUMN "RETTIGHETTYPE"."BELOPKODE" IS 'Referanse til BELOPTYPE';
COMMENT ON COLUMN "RETTIGHETTYPE"."RANGNR" IS 'Rangering av rettighettyper. Et manuelt satt nr som viser  etter hvilken rang vedtak skal presenteres i samme brev.';
COMMENT ON COLUMN "RETTIGHETTYPE"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_KONTERBAR" IS 'Status konterbar';
COMMENT ON COLUMN "RETTIGHETTYPE"."TRANSAKSJONSKODE_FORSKUDD" IS 'Transaksjonskode forskudd. Gir transaksjonskode hvis utbetalingen er et forskudd';
COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETNAVN_KORT" IS 'Kort navn på rettighettypen';
COMMENT ON COLUMN "RETTIGHETTYPE"."FORSKUDD_BETPLAN" IS 'Flagg som forteller hvorvidt forskuddsutbetaling tillates i.f.m. betalingsplan';
COMMENT ON COLUMN "RETTIGHETTYPE"."SATSVALG" IS 'Satsvalg';
COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_TILTAK" IS 'Må være på tiltak for å bruke rettighettype';
COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_START_VEDTAK" IS 'Rettighet som kan opprettes ved start vedtaksbehandling';
COMMENT ON COLUMN "RETTIGHETTYPE"."BILAG_KREVES_JN" IS 'Bilag Kreves Jn';
COMMENT ON COLUMN "RETTIGHETTYPE"."BETPLAN_JN" IS 'Betplan Jn';
COMMENT ON COLUMN "RETTIGHETTYPE"."GJELDERKODE" IS 'Gjelder person, arbeidsgiver, behandler';
COMMENT ON TABLE "RETTIGHETTYPE"  IS 'Hvilke rettighettyper som finnes i Arena.  Beløpkode finnes her eller i Beløpttypevariant. Rettighettyper uten kontering har nei i status_konterbar.';


--------------------------------------------------------
--  DDL for Table SAKSOPPLYSNINGTYPE
--------------------------------------------------------

CREATE TABLE "SAKSOPPLYSNINGTYPE"
(	"SAKSOPPLYSNINGKODE" VARCHAR2(10),
     "SAKSOPPLYSNINGNAVN" VARCHAR2(30),
     "SKJERMBILDETEKST" VARCHAR2(255),
     "BESKRIVELSE" VARCHAR2(255),
     "STATUS_REPETERBAR" VARCHAR2(1) DEFAULT 'N',
     "STATUS_SOKEKNAPPNULL" VARCHAR2(1),
     "SOKEKNAPPNAVN" VARCHAR2(10),
     "SOKEBILDE" VARCHAR2(30),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     CONSTRAINT "SOPTYP_PK" PRIMARY KEY ("SAKSOPPLYSNINGKODE")
) ;

COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."SAKSOPPLYSNINGKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."SAKSOPPLYSNINGNAVN" IS 'Saksopplysningnavn';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."SKJERMBILDETEKST" IS 'Tekststreng for visning i skjermbilde';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."STATUS_REPETERBAR" IS 'Referanse til STATUS_JN, J = repeterbar, N = ikke repeterbar';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."STATUS_SOKEKNAPPNULL" IS 'Referanse til STATUS_JN';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."SOKEKNAPPNAVN" IS 'Søkeknappnavn';
COMMENT ON COLUMN "SAKSOPPLYSNINGTYPE"."SOKEBILDE" IS 'Søkebilde, angir skjermbildet som skal brukes til søk';
COMMENT ON TABLE "SAKSOPPLYSNINGTYPE"  IS 'Angir typer av saksopplysninger';


--------------------------------------------------------
--  DDL for Table SAKSTATUS
--------------------------------------------------------

CREATE TABLE "SAKSTATUS"
(	"SAKSTATUSKODE" VARCHAR2(5),
     "SAKSTATUSNAVN" VARCHAR2(30),
     "FLYTTES_JN" VARCHAR2(1) DEFAULT 'J',
     CONSTRAINT "SAKSTAT_PK" PRIMARY KEY ("SAKSTATUSKODE")
) ;

COMMENT ON COLUMN "SAKSTATUS"."SAKSTATUSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "SAKSTATUS"."SAKSTATUSNAVN" IS 'Sakstatusnavn';
COMMENT ON COLUMN "SAKSTATUS"."FLYTTES_JN" IS 'Markerer om sakstatus kan være med i kontorflytting av saker';
COMMENT ON TABLE "SAKSTATUS"  IS 'Skal kunne karakterisere sakens tilstand';

--------------------------------------------------------
--  DDL for Table SAKSTYPE
--------------------------------------------------------

CREATE TABLE "SAKSTYPE"
(	"SAKSKODE" VARCHAR2(10),
     "SAKSTYPENAVN" VARCHAR2(30),
     "ARKIVNOKKEL" VARCHAR2(10),
     "LUKKES_JN" VARCHAR2(1) DEFAULT 'N',
     "ANT_DAGER_FOER_LUKK" NUMBER(5,0),
     "HISTORISERES_JN" VARCHAR2(1) DEFAULT 'N',
     "ANT_DAGER_FOER_HIST" NUMBER(5,0),
     "FLYTTES_JN" VARCHAR2(1) DEFAULT 'J',
     "SPESIAL_FLYTTES_JN" VARCHAR2(1) DEFAULT 'J',
     "LOGG_FLYTT_OPPGAVE_JN" VARCHAR2(1) DEFAULT 'N',
     "KORTNAVN" VARCHAR2(10),
     "TEMASAK_JN" VARCHAR2(1) DEFAULT 'J',
     "TEMANAVN" VARCHAR2(30),
     "EKSTERN_JN" VARCHAR2(1) DEFAULT 'J',
     "FEILUTBETALING_JN" VARCHAR2(1) DEFAULT 'N',
     "OPPRETT_MANUELT_JN" VARCHAR2(1) DEFAULT 'N',
     "DATO_GYLDIG_FRA" DATE DEFAULT to_date('01-01-2000 00:00:00','DD-MM-YYYY HH24:MI:SS'),
     "DATO_GYLDIG_TIL" DATE,
     "PROSESSGRUPPE" VARCHAR2(50),
     "KLAGE_SENDES" VARCHAR2(100),
     "GJELDERKODE" VARCHAR2(10),
     "BRUK_FULLMEKTIG_BREV" VARCHAR2(1) DEFAULT 'N',
     CONSTRAINT "SAKSTYP_PK" PRIMARY KEY ("SAKSKODE")
) ;

COMMENT ON COLUMN "SAKSTYPE"."SAKSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "SAKSTYPE"."SAKSTYPENAVN" IS 'Sakstypenavn';
COMMENT ON COLUMN "SAKSTYPE"."ARKIVNOKKEL" IS 'Angir et oppgitt arkivnummer';
COMMENT ON COLUMN "SAKSTYPE"."LUKKES_JN" IS 'Angir om sakstypen skal kunne lukkes fra batchjobb.';
COMMENT ON COLUMN "SAKSTYPE"."ANT_DAGER_FOER_LUKK" IS 'Antall dager inaktiv før en sak kan lukkes.';
COMMENT ON COLUMN "SAKSTYPE"."HISTORISERES_JN" IS 'Angir om sakstypen skal kunne historiseres.';
COMMENT ON COLUMN "SAKSTYPE"."ANT_DAGER_FOER_HIST" IS 'Antall dager lukket før en sak kan historiseres.';
COMMENT ON COLUMN "SAKSTYPE"."FLYTTES_JN" IS 'Markerer om sakstypen kan være med i flytting av saker';
COMMENT ON COLUMN "SAKSTYPE"."SPESIAL_FLYTTES_JN" IS 'Angir om sakstypen kan flyttes til og/eller fra et spesialkontor ifbm. kontorflytting.';
COMMENT ON COLUMN "SAKSTYPE"."LOGG_FLYTT_OPPGAVE_JN" IS 'Logg når oppgaven flyttes J/N';
COMMENT ON COLUMN "SAKSTYPE"."KORTNAVN" IS 'Kort navn på sakstypen';
COMMENT ON COLUMN "SAKSTYPE"."TEMASAK_JN" IS 'Kan sakstypen brukes som tema, J/N';
COMMENT ON COLUMN "SAKSTYPE"."TEMANAVN" IS 'Navn på tema for sakstypen';
COMMENT ON COLUMN "SAKSTYPE"."EKSTERN_JN" IS 'Ekstern sakstype J/N';
COMMENT ON COLUMN "SAKSTYPE"."FEILUTBETALING_JN" IS 'Kan feilutbetaling gjøres på sakstype J/N';
COMMENT ON COLUMN "SAKSTYPE"."OPPRETT_MANUELT_JN" IS 'Kan sakstype opprettes manuelt i skjermbildet Registrer henvendelse J/N';
COMMENT ON COLUMN "SAKSTYPE"."DATO_GYLDIG_FRA" IS 'Dato gyldig fra';
COMMENT ON COLUMN "SAKSTYPE"."DATO_GYLDIG_TIL" IS 'Dato gyldig til';
COMMENT ON COLUMN "SAKSTYPE"."PROSESSGRUPPE" IS 'Prosessgruppe i FrameSolution';
COMMENT ON COLUMN "SAKSTYPE"."KLAGE_SENDES" IS 'Referanse til EDB_DEFAULT_VALUES hvor klageinstans er registrert.';
COMMENT ON COLUMN "SAKSTYPE"."GJELDERKODE" IS 'Sakstypen gjelder for, person. behandler, arbeidsgiver';
COMMENT ON COLUMN "SAKSTYPE"."BRUK_FULLMEKTIG_BREV" IS 'Omhandler om eller hvilken fullmektig brev sendes til. ';
COMMENT ON TABLE "SAKSTYPE"  IS 'Kodetabell for å karakterisere saker';


--------------------------------------------------------
--  DDL for Table SATS
--------------------------------------------------------

CREATE TABLE "SATS"
(	"SATSKODE" VARCHAR2(5),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "BELOP" NUMBER,
     "MAALEENHET" VARCHAR2(5),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     CONSTRAINT "SATS_PK" PRIMARY KEY ("SATSKODE", "DATO_FRA")
);

COMMENT ON COLUMN "SATS"."SATSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "SATS"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
COMMENT ON COLUMN "SATS"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
COMMENT ON COLUMN "SATS"."BELOP" IS 'Beløp';
COMMENT ON COLUMN "SATS"."MAALEENHET" IS 'Referanse til MAALEENHETVERDITYPE';
COMMENT ON TABLE "SATS"  IS 'Ulike satser som utgangspunkt for beregninger';



--------------------------------------------------------
--  DDL for Table TRANSAKSJONTYPE
--------------------------------------------------------

CREATE TABLE "TRANSAKSJONTYPE"
(	"TRANSAKSJONSKODE" VARCHAR2(5),
     "TRANSAKSJONSTYPENAVN" VARCHAR2(30),
     "TRANSTYPENAVN" VARCHAR2(30),
     "TRANSGRUPPEKODE" VARCHAR2(5),
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     CONSTRAINT "TRANSTYP_PK" PRIMARY KEY ("TRANSAKSJONSKODE")
) ;

COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSAKSJONSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSAKSJONSTYPENAVN" IS 'Transaksjonstypenavn';
COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSTYPENAVN" IS 'Transtypenavn';
COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSGRUPPEKODE" IS 'Referanse til TRANSAKSJONGRUPPE';
COMMENT ON TABLE "TRANSAKSJONTYPE"  IS 'Lovlige transaksjonstyper';

--------------------------------------------------------
--  DDL for Table UTFALLTYPE
--------------------------------------------------------

CREATE TABLE "UTFALLTYPE"
(	"UTFALLKODE" VARCHAR2(10),
     "UTFALLNAVN" VARCHAR2(30),
     "UTFALLTEKST" VARCHAR2(255),
     CONSTRAINT "UTFTYP_PK" PRIMARY KEY ("UTFALLKODE")
);

COMMENT ON COLUMN "UTFALLTYPE"."UTFALLKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "UTFALLTYPE"."UTFALLNAVN" IS 'Utfallnavn';
COMMENT ON COLUMN "UTFALLTYPE"."UTFALLTEKST" IS 'Utfalltekst';
COMMENT ON TABLE "UTFALLTYPE"  IS 'Hvilke konklusoner kan trekkes';


--------------------------------------------------------
--  DDL for Table VEDTAKFAKTATYPE
--------------------------------------------------------

CREATE TABLE "VEDTAKFAKTATYPE"
(	"VEDTAKFAKTAKODE" VARCHAR2(10),
     "SKJERMBILDETEKST" VARCHAR2(255),
     "STATUS_KVOTEBRUK" VARCHAR2(1),
     "STATUS_OVERSIKT" VARCHAR2(1),
     "VEDTAKFAKTANAVN" VARCHAR2(30),
     "BESKRIVELSE" VARCHAR2(255),
     "ORACLETYPE" VARCHAR2(10),
     "FELTLENGDE" NUMBER(3,0),
     "AVSNITT_ID_LEDETEKST" NUMBER,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     CONSTRAINT "VEDFAKTTYP_PK" PRIMARY KEY ("VEDTAKFAKTAKODE")
) ;

COMMENT ON COLUMN "VEDTAKFAKTATYPE"."VEDTAKFAKTAKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."SKJERMBILDETEKST" IS 'Tekststreng for visning i skjermbilde';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."STATUS_KVOTEBRUK" IS 'Status kvotebruk';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."STATUS_OVERSIKT" IS 'Status oversikt. Brukes til å sjekke om elementet skal vises i liste på modul VF_20_beregnstonad';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."VEDTAKFAKTANAVN" IS 'Vedtakfaktanavn';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."ORACLETYPE" IS 'Oracletype';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."FELTLENGDE" IS 'Feltlengde';
COMMENT ON COLUMN "VEDTAKFAKTATYPE"."AVSNITT_ID_LEDETEKST" IS 'Referanse til AVSNITT';
COMMENT ON TABLE "VEDTAKFAKTATYPE"  IS 'Typer av vedtakfakta som er lovlige';


--------------------------------------------------------
--  DDL for Table VEDTAKSTATUS
--------------------------------------------------------

CREATE TABLE "VEDTAKSTATUS"
(	"VEDTAKSTATUSKODE" VARCHAR2(5),
     "VEDTAKSTATUSNAVN" VARCHAR2(30),
     "BESKRIVELSE" VARCHAR2(255),
     CONSTRAINT "VEDSTAT_PK" PRIMARY KEY ("VEDTAKSTATUSKODE")
)  ;

COMMENT ON COLUMN "VEDTAKSTATUS"."VEDTAKSTATUSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "VEDTAKSTATUS"."VEDTAKSTATUSNAVN" IS 'Vedtakstatusnavn';
COMMENT ON COLUMN "VEDTAKSTATUS"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON TABLE "VEDTAKSTATUS"  IS 'Angir hvilken fase et vedtak er i.';


--------------------------------------------------------
--  DDL for Table VEDTAKTYPE
--------------------------------------------------------

CREATE TABLE "VEDTAKTYPE"
(    "VEDTAKTYPEKODE" VARCHAR2(10),
     "VEDTAKTYPENAVN" VARCHAR2(30),
     "BESKRIVELSE" VARCHAR2(255),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "AVSNITTLISTEKODE_VALGFRI" VARCHAR2(20),
     CONSTRAINT "VEDTYP_PK" PRIMARY KEY ("VEDTAKTYPEKODE")
);

COMMENT ON COLUMN "VEDTAKTYPE"."VEDTAKTYPEKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "VEDTAKTYPE"."VEDTAKTYPENAVN" IS 'Vedtaktypenavn';
COMMENT ON COLUMN "VEDTAKTYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON COLUMN "VEDTAKTYPE"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
COMMENT ON COLUMN "VEDTAKTYPE"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
COMMENT ON COLUMN "VEDTAKTYPE"."AVSNITTLISTEKODE_VALGFRI" IS 'Referanse til AVSNITTLISTE. Ikke i bruk';
COMMENT ON TABLE "VEDTAKTYPE"  IS 'Angir lovlige vedtakstyper';


--------------------------------------------------------
--  DDL for Table VILKAARSTATUS
--------------------------------------------------------

CREATE TABLE "VILKAARSTATUS"
(	"VILKAARSTATUSKODE" VARCHAR2(1),
     "VILKAARSTATUSNAVN" VARCHAR2(30),
     CONSTRAINT "VILKST_PK" PRIMARY KEY ("VILKAARSTATUSKODE")
) ;

COMMENT ON COLUMN "VILKAARSTATUS"."VILKAARSTATUSKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "VILKAARSTATUS"."VILKAARSTATUSNAVN" IS 'Vilkårstatusnavn';
COMMENT ON TABLE "VILKAARSTATUS"  IS 'Kode for å angi status for et vilkår';




--------------------------------------------------------
--  DDL for Table VILKAARTYPE
--------------------------------------------------------

CREATE TABLE "VILKAARTYPE"
(	"VILKAARKODE" VARCHAR2(10),
     "SKJERMBILDETEKST" VARCHAR2(100),
     "STATUS_OBLIG" VARCHAR2(1),
     "VILKAARNAVN" VARCHAR2(30),
     "BESKRIVELSE" VARCHAR2(255),
     "URL_HJELPEREFERANSE" VARCHAR2(2000),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "URL_FORSKRIFTTEKST" VARCHAR2(2000),
     "URL_LOVTEKST" VARCHAR2(2000),
     "URL_RUNDSKRIVTEKST" VARCHAR2(2000),
     "DATO_FRA" DATE DEFAULT TO_DATE('01-01-2001','DD-MM-YYYY'),
     "DATO_TIL" DATE DEFAULT TO_DATE('23-03-2099','DD-MM-YYYY'),
     "VILKAARREGEL" VARCHAR2(100),
     "GRUPPE" VARCHAR2(30)
) ;

COMMENT ON COLUMN "VILKAARTYPE"."VILKAARKODE" IS 'Kode som entydig identifiserer en typeverdi';
COMMENT ON COLUMN "VILKAARTYPE"."SKJERMBILDETEKST" IS 'Tekststreng for visning i skjermbilde';
COMMENT ON COLUMN "VILKAARTYPE"."STATUS_OBLIG" IS 'Status oblig';
COMMENT ON COLUMN "VILKAARTYPE"."VILKAARNAVN" IS 'Vilkårnavn';
COMMENT ON COLUMN "VILKAARTYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
COMMENT ON COLUMN "VILKAARTYPE"."URL_HJELPEREFERANSE" IS 'Url til hjelpereferanse';
COMMENT ON COLUMN "VILKAARTYPE"."URL_FORSKRIFTTEKST" IS 'Url til forskrifttekst';
COMMENT ON COLUMN "VILKAARTYPE"."URL_LOVTEKST" IS 'Url til lovtekst';
COMMENT ON COLUMN "VILKAARTYPE"."URL_RUNDSKRIVTEKST" IS 'Url rundskrivtekst';
COMMENT ON COLUMN "VILKAARTYPE"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
COMMENT ON COLUMN "VILKAARTYPE"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
COMMENT ON COLUMN "VILKAARTYPE"."VILKAARREGEL" IS 'Vilkårregel angir  regelnavn for regel i Regelmotor';
COMMENT ON COLUMN "VILKAARTYPE"."GRUPPE" IS 'gruppere flere vilkår sammen i en gruppe for bruk i automatisk vilkårsvurderingsregler og i regler for setting av utfall.';
COMMENT ON TABLE "VILKAARTYPE"  IS 'Lovlige vilkårtyper';


--------------------------------------------------------------------------------
-- ARENADATA
--------------------------------------------------------------------------------

