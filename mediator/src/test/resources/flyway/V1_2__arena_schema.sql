--------------------------------------------------------
--  DDL for Table PERSON
--------------------------------------------------------

CREATE TABLE "PERSON"
(	"PERSON_ID" NUMBER,
     "FODSELSDATO" DATE,
     "STATUS_DNR" VARCHAR2(1) DEFAULT 'N',
     "PERSONNR" NUMBER(5,0),
     "FODSELSNR" VARCHAR2(11),
     "ETTERNAVN" VARCHAR2(30),
     "FORNAVN" VARCHAR2(30),
     "DATO_FRA" DATE DEFAULT sysdate,
     "STATUS_SAMTYKKE" VARCHAR2(1) DEFAULT 'N',
     "DATO_SAMTYKKE" DATE,
     "VERNEPLIKTKODE" VARCHAR2(5) DEFAULT NULL,
     "MAALFORM" VARCHAR2(2) DEFAULT 'NO',
     "LANDKODE_STATSBORGER" VARCHAR2(2),
     "KONTONUMMER" VARCHAR2(11),
     "STATUS_BILDISP" VARCHAR2(1),
     "FORMIDLINGSGRUPPEKODE" VARCHAR2(5) DEFAULT 'ISERV',
     "VIKARGRUPPEKODE" VARCHAR2(5) DEFAULT 'IVIK',
     "KVALIFISERINGSGRUPPEKODE" VARCHAR2(5) DEFAULT 'IVURD',
     "RETTIGHETSGRUPPEKODE" VARCHAR2(5) DEFAULT 'IYT',
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "AETATORGENHET" VARCHAR2(8),
     "LONNSLIPP_EPOST" VARCHAR2(1),
     "DATO_OVERFORT_AMELDING" DATE,
     "DATO_SIST_INAKTIV" DATE,
     "BEGRUNNELSE_FORMIDLINGSGRUPPE" VARCHAR2(2000),
     "HOVEDMAALKODE" VARCHAR2(10),
     "BRUKERID_NAV_KONTAKT" VARCHAR2(8),
     "FR_KODE" VARCHAR2(2),
     "ER_DOED" VARCHAR2(1),
     "PERSON_ID_STATUS" VARCHAR2(20) DEFAULT 'AKTIV',
     "SPERRET_KOMMENTAR" VARCHAR2(500),
     "SPERRET_TIL" DATE,
     "SPERRET_DATO" DATE,
     "SPERRET_AV" VARCHAR2(8)
) ;

COMMENT ON COLUMN "PERSON"."PERSON_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "PERSON"."FODSELSDATO" IS 'Fødselsdato';
COMMENT ON COLUMN "PERSON"."STATUS_DNR" IS 'Settes J hvis FODSELSNR er et DNR';
COMMENT ON COLUMN "PERSON"."PERSONNR" IS 'Norsk personnummer eller D-nummer';
COMMENT ON COLUMN "PERSON"."FODSELSNR" IS 'Norsk fødselsnummer';
COMMENT ON COLUMN "PERSON"."ETTERNAVN" IS 'Etternavn';
COMMENT ON COLUMN "PERSON"."FORNAVN" IS 'Fornavn evt med mellomnavn';
COMMENT ON COLUMN "PERSON"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
COMMENT ON COLUMN "PERSON"."STATUS_SAMTYKKE" IS 'Status samtykke';
COMMENT ON COLUMN "PERSON"."DATO_SAMTYKKE" IS 'Dato for når siste samtykke er gitt';
COMMENT ON COLUMN "PERSON"."VERNEPLIKTKODE" IS 'Kode for gjennomført verneplikt';
COMMENT ON COLUMN "PERSON"."MAALFORM" IS 'Referanse til EDB_LANGUAGE';
COMMENT ON COLUMN "PERSON"."LANDKODE_STATSBORGER" IS 'Referanse til LAND';
COMMENT ON COLUMN "PERSON"."KONTONUMMER" IS 'Ikke i bruk. Flyttet til kommbruk type: NOKTO Kontonummer for utbetalinger fra Aetat';
COMMENT ON COLUMN "PERSON"."STATUS_BILDISP" IS 'Disponerer bil';
COMMENT ON COLUMN "PERSON"."FORMIDLINGSGRUPPEKODE" IS 'Referanse til FORMIDLINGSGRUPPETYPE personen tilhører';
COMMENT ON COLUMN "PERSON"."VIKARGRUPPEKODE" IS 'Ikke i bruk. Referanse til VIKARGRUPPETYPE';
COMMENT ON COLUMN "PERSON"."KVALIFISERINGSGRUPPEKODE" IS 'Referanse til KVALIFISERINGSGRUPPETYPE. Kalles nå Servicegruppe';
COMMENT ON COLUMN "PERSON"."RETTIGHETSGRUPPEKODE" IS 'Referanse til RETTIGHETSGRUPPETYPE';
COMMENT ON COLUMN "PERSON"."AETATORGENHET" IS 'Referanse til ORGUNITINSTANCE';
COMMENT ON COLUMN "PERSON"."LONNSLIPP_EPOST" IS 'Angir om en person skal motta lønnslipp via epost';
COMMENT ON COLUMN "PERSON"."DATO_OVERFORT_AMELDING" IS 'Dato for første overføring til Amelding';
COMMENT ON COLUMN "PERSON"."DATO_SIST_INAKTIV" IS 'Dato sist inaktiv';
COMMENT ON COLUMN "PERSON"."BEGRUNNELSE_FORMIDLINGSGRUPPE" IS 'Evt saksbehandlers begrunnelse ved setting av formidlingsgruppe';
COMMENT ON COLUMN "PERSON"."HOVEDMAALKODE" IS 'Rereranse til HOVEDMAAL';
COMMENT ON COLUMN "PERSON"."BRUKERID_NAV_KONTAKT" IS 'Referanse til ORGUNITINSTANCE. Kontaktperson hos NAV';
COMMENT ON COLUMN "PERSON"."FR_KODE" IS 'Koding av fortrolige adresser fra folkeregisteret';
COMMENT ON COLUMN "PERSON"."ER_DOED" IS 'Er død';
COMMENT ON COLUMN "PERSON"."PERSON_ID_STATUS" IS 'Status for denne personforekomsten/person_id, ikke generelt for person';
COMMENT ON COLUMN "PERSON"."SPERRET_KOMMENTAR" IS 'Evt. kommentar om sperringen';
COMMENT ON COLUMN "PERSON"."SPERRET_TIL" IS 'Evt. slutt dato for sperringen';
COMMENT ON COLUMN "PERSON"."SPERRET_DATO" IS 'Dato sperren ble satt';
COMMENT ON COLUMN "PERSON"."SPERRET_AV" IS 'Saksbeh. ident for den som etablerte sperren.';
COMMENT ON TABLE "PERSON"  IS 'Tabellen omfatter alle personer som NAV har et forhold til';


--------------------------------------------------------
--  DDL for Table SAK
--------------------------------------------------------

CREATE TABLE "SAK"
(	"SAK_ID" NUMBER,
     "SAKSKODE" VARCHAR2(10) DEFAULT 'INAKT',
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "TABELLNAVNALIAS" VARCHAR2(10),
     "OBJEKT_ID" NUMBER,
     "AAR" NUMBER(4,0) DEFAULT EXTRACT(YEAR FROM CURRENT_DATE),
     "LOPENRSAK" NUMBER(7,0),
     "DATO_AVSLUTTET" DATE,
     "SAKSTATUSKODE" VARCHAR2(5),
     "ARKIVNOKKEL" VARCHAR2(7),
     "AETATENHET_ARKIV" VARCHAR2(8),
     "ARKIVHENVISNING" VARCHAR2(255),
     "BRUKERID_ANSVARLIG" VARCHAR2(8),
     "AETATENHET_ANSVARLIG" VARCHAR2(8),
     "OBJEKT_KODE" VARCHAR2(10),
     "STATUS_ENDRET" DATE,
     "PARTISJON" NUMBER(8,0),
     "ER_UTLAND" VARCHAR2(1) DEFAULT 'N'
) ;

COMMENT ON COLUMN "SAK"."SAK_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "SAK"."SAKSKODE" IS 'Referanse til SAKSTYPE';
COMMENT ON COLUMN "SAK"."TABELLNAVNALIAS" IS 'Hovedaktøren i saken Person eller arbeidsgiver.Kortnavn for tabellen som eier posten. Gir sammen med objekt_id eller objekt_kode eierobjektet. FK til Objekttype (UK).';
COMMENT ON COLUMN "SAK"."OBJEKT_ID" IS 'Objekt_id angir sammen med tabellnavn eller tabellnavnalias eierobjektet';
COMMENT ON COLUMN "SAK"."AAR" IS 'År inngår i saksnummer';
COMMENT ON COLUMN "SAK"."LOPENRSAK" IS 'Løpenr for sak innen et år';
COMMENT ON COLUMN "SAK"."DATO_AVSLUTTET" IS 'Dato avsluttet';
COMMENT ON COLUMN "SAK"."SAKSTATUSKODE" IS 'Referanse til SAKSTATUS';
COMMENT ON COLUMN "SAK"."ARKIVNOKKEL" IS 'Angir et oppgitt arkivnummer';
COMMENT ON COLUMN "SAK"."AETATENHET_ARKIV" IS 'Referanse til ORGUNITINSTANCE';
COMMENT ON COLUMN "SAK"."ARKIVHENVISNING" IS 'Arkivhenvisning';
COMMENT ON COLUMN "SAK"."BRUKERID_ANSVARLIG" IS 'Generelt ansvarlig saksbehandler. For tiltak: Signaturen til den saksbehandler som er ansvarlig for å planlegge,';
COMMENT ON COLUMN "SAK"."AETATENHET_ANSVARLIG" IS 'Generelt ansvarlig Aetat-enhet';
COMMENT ON COLUMN "SAK"."OBJEKT_KODE" IS 'Objekt_kode angir sammen med tabellnavn eller tabellnavnalias eierobjektet';
COMMENT ON COLUMN "SAK"."STATUS_ENDRET" IS 'Dato for siste endring av SAKSTATUSKODE';
COMMENT ON COLUMN "SAK"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "SAK"."ER_UTLAND" IS 'For å kunne merke oppgaver som UTLAND i ARENA';
COMMENT ON TABLE "SAK"  IS 'Alle saker i Arena';


--------------------------------------------------------
--  DDL for Table VEDTAK
--------------------------------------------------------

CREATE TABLE "VEDTAK"
(	"VEDTAK_ID" NUMBER,
     "SAK_ID" NUMBER,
     "VEDTAKSTATUSKODE" VARCHAR2(5),
     "VEDTAKTYPEKODE" VARCHAR2(10),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "UTFALLKODE" VARCHAR2(10),
     "BEGRUNNELSE" VARCHAR2(4000),
     "BRUKERID_ANSVARLIG" VARCHAR2(8),
     "AETATENHET_BEHANDLER" VARCHAR2(8),
     "AAR" NUMBER(4,0) DEFAULT 2000,
     "LOPENRSAK" NUMBER(7,0),
     "LOPENRVEDTAK" NUMBER(3,0),
     "RETTIGHETKODE" VARCHAR2(10),
     "AKTFASEKODE" VARCHAR2(10),
     "BREV_ID" NUMBER,
     "TOTALBELOP" NUMBER(8,2),
     "DATO_MOTTATT" DATE,
     "VEDTAK_ID_RELATERT" NUMBER,
     "AVSNITTLISTEKODE_VALGT" VARCHAR2(20),
     "HANDLINGSPLAN_ID" NUMBER ,
     "PERSON_ID" NUMBER,
     "BRUKERID_BESLUTTER" VARCHAR2(8),
     "STATUS_SENSITIV" VARCHAR2(1),
     "VEDLEGG_BETPLAN" VARCHAR2(1),
     "PARTISJON" NUMBER(8,0),
     "OPPSUMMERING_SB2" VARCHAR2(4000),
     "DATO_UTFORT_DEL1" DATE,
     "DATO_UTFORT_DEL2" DATE,
     "OVERFORT_NAVI" VARCHAR2(1),
     "FRA_DATO" DATE,
     "TIL_DATO" DATE,
     "SF_OPPFOLGING_ID" NUMBER,
     "STATUS_SOSIALDATA" VARCHAR2(1) DEFAULT 'N',
     "KONTOR_SOSIALDATA" VARCHAR2(8),
     "TEKSTVARIANTKODE" VARCHAR2(20),
     "VALGT_BESLUTTER" VARCHAR2(8),
     "TEKNISK_VEDTAK" VARCHAR2(1),
     "DATO_INNSTILT" DATE,
     "ER_UTLAND" VARCHAR2(1) DEFAULT 'N' NOT NULL
);

COMMENT ON COLUMN "VEDTAK"."VEDTAK_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "VEDTAK"."SAK_ID" IS 'Referanse til SAK';
COMMENT ON COLUMN "VEDTAK"."VEDTAKSTATUSKODE" IS 'Referanse til VEDTAKSTATUS';
COMMENT ON COLUMN "VEDTAK"."VEDTAKTYPEKODE" IS 'Referanse til KRAVTYPE';
COMMENT ON COLUMN "VEDTAK"."UTFALLKODE" IS 'Referanse til UTFALLTYPE';
COMMENT ON COLUMN "VEDTAK"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
COMMENT ON COLUMN "VEDTAK"."BRUKERID_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE. Generelt ansvarlig saksbehandler';
COMMENT ON COLUMN "VEDTAK"."AETATENHET_BEHANDLER" IS 'Referanse til ORGUNITINSTANCE. Enhet somn behandler vedtak';
COMMENT ON COLUMN "VEDTAK"."AAR" IS 'Referanse til SAK. Angir år i saken';
COMMENT ON COLUMN "VEDTAK"."LOPENRSAK" IS 'Referanse til SAK. Angir løpenummer i en sak';
COMMENT ON COLUMN "VEDTAK"."LOPENRVEDTAK" IS 'Løpenrvedtak';
COMMENT ON COLUMN "VEDTAK"."RETTIGHETKODE" IS 'Referanse til RETTIGHETTYPE';
COMMENT ON COLUMN "VEDTAK"."AKTFASEKODE" IS 'Referanse til AKTIVITETFASE';
COMMENT ON COLUMN "VEDTAK"."BREV_ID" IS 'Referanse til BREV';
COMMENT ON COLUMN "VEDTAK"."TOTALBELOP" IS 'Totalbeløp';
COMMENT ON COLUMN "VEDTAK"."DATO_MOTTATT" IS 'Dato mottatt';
COMMENT ON COLUMN "VEDTAK"."VEDTAK_ID_RELATERT" IS 'Referanse til VEDTAK. Peker til ''opprinnelig'' vedtak ved endring, gjenopptak etc';
COMMENT ON COLUMN "VEDTAK"."AVSNITTLISTEKODE_VALGT" IS 'Referanse til AVSNITTLISTE, ikke i bruk';
COMMENT ON COLUMN "VEDTAK"."HANDLINGSPLAN_ID" IS 'Referanse til utgÃ¥tt tabell HANDLINGSPLAN(PK-36568)';
COMMENT ON COLUMN "VEDTAK"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "VEDTAK"."BRUKERID_BESLUTTER" IS 'Referanse til ORGUNITINSTANCE. Brukerid for besluttende saksbehandler';
COMMENT ON COLUMN "VEDTAK"."STATUS_SENSITIV" IS 'Status sensitiv';
COMMENT ON COLUMN "VEDTAK"."VEDLEGG_BETPLAN" IS 'Vedlegg Betplan';
COMMENT ON COLUMN "VEDTAK"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "VEDTAK"."OPPSUMMERING_SB2" IS 'Oppsummering for vedtaket del 2';
COMMENT ON COLUMN "VEDTAK"."DATO_UTFORT_DEL1" IS 'Dato for første del av vilkårssvurdering for vedtak hvor vilkårsvurdering er delt mellom lokal og forvaltning. Brukt i eksterne vedtak';
COMMENT ON COLUMN "VEDTAK"."DATO_UTFORT_DEL2" IS 'Dato for andre del av vilkårssvurdering for vedtak hvor vilkårsvurdering er delt mellom lokal og forvaltning. Brukt i eksterne vedtak';
COMMENT ON COLUMN "VEDTAK"."OVERFORT_NAVI" IS 'Overført NAVI J/N, J dersom vedtaket er overført NAVI for innkreving av feilutbetaling';
COMMENT ON COLUMN "VEDTAK"."FRA_DATO" IS 'Denormalisert vedtaksfakta FDAT';
COMMENT ON COLUMN "VEDTAK"."TIL_DATO" IS 'Denormalisert vedtaksfakta TDAT';
COMMENT ON COLUMN "VEDTAK"."SF_OPPFOLGING_ID" IS 'Referanse til SF_OPPFOLGING';
COMMENT ON COLUMN "VEDTAK"."STATUS_SOSIALDATA" IS 'Status sosialdata, kontorsperret informasjon på vedtaket.';
COMMENT ON COLUMN "VEDTAK"."KONTOR_SOSIALDATA" IS 'Referanse til ORGUNITINSTANSE, hvilke kontor som har kontorsperret vedtaket.';
COMMENT ON COLUMN "VEDTAK"."TEKSTVARIANTKODE" IS 'Tekstvariant saksbehandleren valgte for vedtaket.';
COMMENT ON COLUMN "VEDTAK"."VALGT_BESLUTTER" IS 'Beslutter saksbehandler valgte for vedtaket.';
COMMENT ON COLUMN "VEDTAK"."TEKNISK_VEDTAK" IS 'Kolonne for å angi om vedtaker er teknisk, og ikke skal generere noe brev.';
COMMENT ON COLUMN "VEDTAK"."DATO_INNSTILT" IS 'Inneholder den dato som vedtakstatuskode settes til INNST. Kolonne settes i trigger VEDTAK_BUR.';
COMMENT ON COLUMN "VEDTAK"."ER_UTLAND" IS 'For å kunne merke oppgaver som UTLAND i ARENA';
COMMENT ON TABLE "VEDTAK"  IS 'Alle vedtak som forberedes, behandles eller er fattet.';


--------------------------------------------------------
--  DDL for Table ANMERKNING
--------------------------------------------------------

CREATE TABLE "ANMERKNING"
(	"ANMERKNINGKODE" VARCHAR2(5),
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "VERDI" NUMBER(5,0),
     "ANMERKNING_ID" NUMBER,
     "TABELLNAVNALIAS" VARCHAR2(10),
     "OBJEKT_ID" NUMBER,
     "VEDTAK_ID" NUMBER,
     "PARTISJON" NUMBER(8,0),
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "VERDI2" NUMBER(5,0)
) ;

COMMENT ON COLUMN "ANMERKNING"."ANMERKNINGKODE" IS 'Referanse til ANMERKNINGTYPE';
COMMENT ON COLUMN "ANMERKNING"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "ANMERKNING"."REG_DATO" IS 'Angir tidspunkt for når raden ble opprettet';
COMMENT ON COLUMN "ANMERKNING"."VERDI" IS 'Flettes inn i subtitusjonsparameter 1 i beskrivelsen i anmerkningtype.';
COMMENT ON COLUMN "ANMERKNING"."ANMERKNING_ID" IS 'Unik ID for anmerkningen';
COMMENT ON COLUMN "ANMERKNING"."TABELLNAVNALIAS" IS 'Angir hva anmerkningen gjelder. Sammen med objekt_id er det en entydig referanse.';
COMMENT ON COLUMN "ANMERKNING"."OBJEKT_ID" IS 'Referanse til det anmerkningen gjelder. Sammen med objekt_id er det en entydig referanse.';
COMMENT ON COLUMN "ANMERKNING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "ANMERKNING"."PARTISJON" IS 'Angir partisjonsnÃ¸kkelen ifbm. Historiseringsbatchen';
COMMENT ON COLUMN "ANMERKNING"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON COLUMN "ANMERKNING"."MOD_DATO" IS 'Angir tidspunkt for når raden sist ble endret';
COMMENT ON COLUMN "ANMERKNING"."VERDI2" IS 'Flettes inn i subtitusjonsparameter 2 i beskrivelsen i anmerkningtype.';
COMMENT ON TABLE "ANMERKNING"  IS 'Inneholder alle forskjellige anmerkninger som kan knyttes til (hovedsakelig) meldekort';


--------------------------------------------------------
--  DDL for Table VEDTAKFAKTA
--------------------------------------------------------

CREATE TABLE "VEDTAKFAKTA"
(	"VEDTAK_ID" NUMBER,
     "VEDTAKFAKTAKODE" VARCHAR2(10),
     "VEDTAKVERDI" VARCHAR2(2000),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "PERSON_ID" NUMBER,
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAKFAKTAKODE" IS 'Referanse til VEDTAKFAKTATYPE';
COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAKVERDI" IS 'Vedtakverdi';
COMMENT ON COLUMN "VEDTAKFAKTA"."REG_DATO" IS 'Angir tidspunktet for når raden ble opprettet';
COMMENT ON COLUMN "VEDTAKFAKTA"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "VEDTAKFAKTA"."MOD_DATO" IS 'Angir tidspunktet for når raden sist ble endret';
COMMENT ON COLUMN "VEDTAKFAKTA"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON COLUMN "VEDTAKFAKTA"."PERSON_ID" IS 'Ikke i bruk lenger. Brukt i hl3 for konvertering';
COMMENT ON COLUMN "VEDTAKFAKTA"."PARTISJON" IS 'Partisjonsnøkkel. Benyttes ikke lenger!';
COMMENT ON TABLE "VEDTAKFAKTA"  IS 'Gjelder enkeltopplysninger knyttet til et vedtak. Kan sammenlignes med attributt for saksopplysning.';


--------------------------------------------------------
--  DDL for Table VILKAARVURDERING
--------------------------------------------------------

CREATE TABLE "VILKAARVURDERING"
(	"VILKAARVURDERING_ID" NUMBER,
     "VEDTAKTYPEKODE" VARCHAR2(10),
     "VILKAARKODE" VARCHAR2(10),
     "VEDTAK_ID" NUMBER,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "RETTIGHETKODE" VARCHAR2(10),
     "AKTFASEKODE" VARCHAR2(10),
     "VILKAARSTATUSKODE" VARCHAR2(1) DEFAULT 'V',
     "VURDERT_AV" VARCHAR2(8),
     "PARTISJON" NUMBER(8,0),
     "BEGRUNNELSE" CLOB,
     "RETUR_JN" VARCHAR2(1) DEFAULT 'N',
     "KOMMENTAR_SB2" VARCHAR2(1000),
     "BEGRUNNELSE_SB2" CLOB,
     "SF_HENDELSE_ID" NUMBER
);

COMMENT ON COLUMN "VILKAARVURDERING"."VILKAARVURDERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "VILKAARVURDERING"."VEDTAKTYPEKODE" IS 'Referanse til LOV_VILKAARTYPE_KRAVTYP';
COMMENT ON COLUMN "VILKAARVURDERING"."VILKAARKODE" IS 'Referanse til LOV_VILKAARTYPE_KRAVTYP';
COMMENT ON COLUMN "VILKAARVURDERING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "VILKAARVURDERING"."RETTIGHETKODE" IS 'Referanse til LOV_VILKAARTYPE_KRAVTYP';
COMMENT ON COLUMN "VILKAARVURDERING"."AKTFASEKODE" IS 'Referanse til LOV_VILKAARTYPE_KRAVTYP';
COMMENT ON COLUMN "VILKAARVURDERING"."VILKAARSTATUSKODE" IS 'Referanse til VILKAARSTATUS';
COMMENT ON COLUMN "VILKAARVURDERING"."VURDERT_AV" IS 'Hvem har vurdert vilkåret, saksbehandler eller Arena(automatisk/beregnet)';
COMMENT ON COLUMN "VILKAARVURDERING"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "VILKAARVURDERING"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
COMMENT ON COLUMN "VILKAARVURDERING"."RETUR_JN" IS 'Retur J/N brukes ved delt vilkårsvurdering';
COMMENT ON COLUMN "VILKAARVURDERING"."KOMMENTAR_SB2" IS 'Kommentar fra saksbehandler to ved delt vurdering';
COMMENT ON COLUMN "VILKAARVURDERING"."BEGRUNNELSE_SB2" IS 'Begrunnelse fra saksbehandler 2 ved delt vurdering';
COMMENT ON COLUMN "VILKAARVURDERING"."SF_HENDELSE_ID" IS 'Referanse til SF_HENDELSE';
COMMENT ON TABLE "VILKAARVURDERING"  IS 'Resultat av at et vilkår er vurdert.';


CREATE TABLE "ATTRIBUTT"
(	"ATTRIBUTT_ID" NUMBER,
     "SAKSOPPLYSNING_ID_EIER" NUMBER,
     "STATUS_SJEKKET_AV" VARCHAR2(1),
     "VERDI" VARCHAR2(2000),
     "ATTRIBUTT_ID_OVERORDNET" NUMBER,
     "ATTRIBUTTYPE_ID" NUMBER,
     "POSISJON" NUMBER(2,0),
     "PARTISJON" NUMBER(8,0),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "BARN_FDATO" DATE
) ;

COMMENT ON COLUMN "ATTRIBUTT"."ATTRIBUTT_ID" IS 'Unik ID';
COMMENT ON COLUMN "ATTRIBUTT"."SAKSOPPLYSNING_ID_EIER" IS 'Referanse til SAKSOPPLYSNING';
COMMENT ON COLUMN "ATTRIBUTT"."STATUS_SJEKKET_AV" IS 'Status sjekket av';
COMMENT ON COLUMN "ATTRIBUTT"."VERDI" IS 'Verdi for attributten (fritekst)';
COMMENT ON COLUMN "ATTRIBUTT"."ATTRIBUTT_ID_OVERORDNET" IS 'Referanse til "master" (oppsummert attributt for alle unerliggende detaljer).';
COMMENT ON COLUMN "ATTRIBUTT"."ATTRIBUTTYPE_ID" IS 'Referanse til ATTRIBUTTYPE';
COMMENT ON COLUMN "ATTRIBUTT"."POSISJON" IS 'Rekkefølgen på attributtene i saksopplysningslisten';
COMMENT ON COLUMN "ATTRIBUTT"."PARTISJON" IS 'Partisjonsnøkkel (ifbm. Historiseringsbatchen).';
COMMENT ON COLUMN "ATTRIBUTT"."BARN_FDATO" IS 'FDATO som DATE verdi derson saksopplysning for BARN';
COMMENT ON TABLE "ATTRIBUTT"  IS 'Alle attributtene relaterte til saksopplysning.';


--------------------------------------------------------
--  DDL for Table BELOP_PR_DAG
--------------------------------------------------------

CREATE TABLE "BELOP_PR_DAG"
(	"BELOP_PR_DAG_ID" NUMBER ,
     "PERSON_ID" NUMBER,
     "VEDTAK_ID" NUMBER,
     "MELDEKORT_ID" NUMBER,
     "DAGSBELOP" NUMBER(12,2),
     "BELOPKODE" VARCHAR2(5),
     "EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
     "AAR" NUMBER(4,0),
     "UBG_DATO_FRA" DATE,
     "UBG_DATO_TIL" DATE,
     "TRANSAKSJONSKODE" VARCHAR2(5),
     "TRANSAKSJONSTEKST" VARCHAR2(60),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8)
) ;

COMMENT ON COLUMN "BELOP_PR_DAG"."BELOP_PR_DAG_ID" IS 'Generert Oracle-sekvens som entydig identifiserer raden';
COMMENT ON COLUMN "BELOP_PR_DAG"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "BELOP_PR_DAG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "BELOP_PR_DAG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT';
COMMENT ON COLUMN "BELOP_PR_DAG"."DAGSBELOP" IS 'Beløp pr dag i perioden multiplisert med 100';
COMMENT ON COLUMN "BELOP_PR_DAG"."BELOPKODE" IS 'Referanse til KONTOTYPE';
COMMENT ON COLUMN "BELOP_PR_DAG"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til BETALINGMOTTAKER';
COMMENT ON COLUMN "BELOP_PR_DAG"."AAR" IS 'År';
COMMENT ON COLUMN "BELOP_PR_DAG"."UBG_DATO_FRA" IS 'Dato periode fra';
COMMENT ON COLUMN "BELOP_PR_DAG"."UBG_DATO_TIL" IS 'Dato periode til';
COMMENT ON COLUMN "BELOP_PR_DAG"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
COMMENT ON COLUMN "BELOP_PR_DAG"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
COMMENT ON COLUMN "BELOP_PR_DAG"."REG_DATO" IS 'Angir tidspunktet for når raden ble opprettet';
COMMENT ON COLUMN "BELOP_PR_DAG"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "BELOP_PR_DAG"."MOD_DATO" IS 'Angir tidspunktet for når raden sist ble endret';
COMMENT ON COLUMN "BELOP_PR_DAG"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON TABLE "BELOP_PR_DAG"  IS 'Tabell for å ta vare på beregnet beløp per kalenderdag i perioden';


--------------------------------------------------------
--  DDL for Table BEREGNINGSLEDD
--------------------------------------------------------

CREATE TABLE "BEREGNINGSLEDD"
(	"BEREGNINGSLEDD_ID" NUMBER,
     "BEREGNINGSLEDDKODE" VARCHAR2(5),
     "DATO_FRA" DATE,
     "PERSON_ID" NUMBER,
     "DATO_TIL" DATE,
     "TABELLNAVNALIAS_KILDE" VARCHAR2(10),
     "OBJEKT_ID_KILDE" NUMBER,
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "VERDI" NUMBER,
     "TILLEGGSKODE" VARCHAR2(5),
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "BEREGNINGSLEDD"."BEREGNINGSLEDD_ID" IS 'Unik ID';
COMMENT ON COLUMN "BEREGNINGSLEDD"."BEREGNINGSLEDDKODE" IS 'Referanse til BEREGNINGSLEDDTYPE';
COMMENT ON COLUMN "BEREGNINGSLEDD"."DATO_FRA" IS 'Fra-dato for gyldighetsperioden til beregningsleddet';
COMMENT ON COLUMN "BEREGNINGSLEDD"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "BEREGNINGSLEDD"."DATO_TIL" IS 'Til-dato for gyldighetsperioden til beregningsleddet';
COMMENT ON COLUMN "BEREGNINGSLEDD"."TABELLNAVNALIAS_KILDE" IS 'Angir hvilken tabell somer kilden til beregningsleddet. Objektet som har sist oppdaterte beregningsleddet (tabellen)';
COMMENT ON COLUMN "BEREGNINGSLEDD"."OBJEKT_ID_KILDE" IS 'Angir id''en til kilden til beregningsleddet';
COMMENT ON COLUMN "BEREGNINGSLEDD"."VERDI" IS 'Angir verdien til beregningsleddet. Knyttet til beregningsleddkoden som angir hva verdien representerer.';
COMMENT ON COLUMN "BEREGNINGSLEDD"."TILLEGGSKODE" IS 'Tilleggskode (detaljeringskode). Angir mer spesifikt hva beregningsleddet gjelder.';
COMMENT ON COLUMN "BEREGNINGSLEDD"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "BEREGNINGSLEDD"  IS 'Beregningsledd er konsekvens av et vedtak som skal benyttes ved beregninger.';


--------------------------------------------------------
--  DDL for Table BEREGNINGSLOGG
--------------------------------------------------------

CREATE TABLE "BEREGNINGSLOGG"
(	"PERSON_ID" NUMBER,
     "VEDTAK_ID" NUMBER,
     "TABELLNAVNALIAS" VARCHAR2(10),
     "OBJEKT_ID" NUMBER(10,0),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "KOMMENTAR" VARCHAR2(30),
     "PARTISJON" NUMBER(8,0),
     "BEREGNINGSLOGG_ID" NUMBER
)  ;

COMMENT ON COLUMN "BEREGNINGSLOGG"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "BEREGNINGSLOGG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "BEREGNINGSLOGG"."TABELLNAVNALIAS" IS 'Angir tabell som beregningen i tillegg er knyttet til';
COMMENT ON COLUMN "BEREGNINGSLOGG"."OBJEKT_ID" IS 'Angir ID''en til raden for tabellen angitt i TABELLNAVNALIAS';
COMMENT ON COLUMN "BEREGNINGSLOGG"."DATO_FRA" IS 'Beregningsledd-perioden for beregningen (fra-dato)';
COMMENT ON COLUMN "BEREGNINGSLOGG"."DATO_TIL" IS 'Beregningsledd-perioden for beregningen (til-dato)';
COMMENT ON COLUMN "BEREGNINGSLOGG"."KOMMENTAR" IS 'Kommentar til beregningen';
COMMENT ON COLUMN "BEREGNINGSLOGG"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "BEREGNINGSLOGG"."BEREGNINGSLOGG_ID" IS 'Generert Oracle-sekvens som entydig identifiserer raden';
COMMENT ON TABLE "BEREGNINGSLOGG"  IS 'Logg over utførte beregninger';


--------------------------------------------------------
--  DDL for Table BETALINGSPLAN
--------------------------------------------------------

CREATE TABLE "BETALINGSPLAN"
(	"VEDTAK_ID" NUMBER,
     "UTBETALINGNR" NUMBER(10,0),
     "DATO_UTBETALING" DATE,
     "STATUS_NYDOK" VARCHAR2(1) DEFAULT 'J',
     "BELOPKODE" VARCHAR2(10),
     "BELOP" NUMBER(8,2),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "STATUS_KLAR" VARCHAR2(1),
     "DATO_BILAGSFRIST" DATE,
     "STATUS_UTBETGRUNNLAG" VARCHAR2(1) DEFAULT 'N',
     "BETALINGSPLAN_ID" NUMBER,
     "BELOP_TIL_UTBETALING" NUMBER(8,2),
     "PARTISJON" NUMBER(8,0),
     "ETTERBETALING" VARCHAR2(1) DEFAULT 'N',
     "BETALINGSPLAN_ID_RELATERT" NUMBER
)  ;

COMMENT ON COLUMN "BETALINGSPLAN"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "BETALINGSPLAN"."UTBETALINGNR" IS 'Løpenummer for vedtakets utbetalinger';
COMMENT ON COLUMN "BETALINGSPLAN"."DATO_UTBETALING" IS 'Dato for utbetalingen';
COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_NYDOK" IS 'Angir om dokumentasjon foreligger (J) eller mangler (N)';
COMMENT ON COLUMN "BETALINGSPLAN"."BELOPKODE" IS 'Refererer til KONTOTYPE';
COMMENT ON COLUMN "BETALINGSPLAN"."BELOP" IS 'Beløpet for betalingsplanen';
COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_KLAR" IS 'Angir om betalingsplanen er klar for utbetaling';
COMMENT ON COLUMN "BETALINGSPLAN"."DATO_BILAGSFRIST" IS 'Frist for mottak av bilag';
COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_UTBETGRUNNLAG" IS 'Angir om betalingsplanen er utbetalt (J) eller ikke (N)';
COMMENT ON COLUMN "BETALINGSPLAN"."BETALINGSPLAN_ID" IS 'Unik ID';
COMMENT ON COLUMN "BETALINGSPLAN"."BELOP_TIL_UTBETALING" IS 'Beløp som skal utbetales';
COMMENT ON COLUMN "BETALINGSPLAN"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "BETALINGSPLAN"."ETTERBETALING" IS 'Angir om betalingsplanen gjelder etterbetaling. Lovlige verdier skal være J/N, og verdi settes default til N.';
COMMENT ON COLUMN "BETALINGSPLAN"."BETALINGSPLAN_ID_RELATERT" IS 'Referanse til annen betalingsplan som inneholder etterbetaling for betalingsplanen';
COMMENT ON TABLE "BETALINGSPLAN"  IS 'Skal gi mulighet for å legge opp en betalingsplan slik at et vedtak kan gi flere utbetalinger.';


--------------------------------------------------------
--  DDL for Table FEILUTBET_OVERFORING_HIST
--------------------------------------------------------

CREATE TABLE "FEILUTBET_OVERFORING_HIST"
(	"FEILUTBET_OVERFORING_ID" NUMBER,
     "FODSELSNR" VARCHAR2(11),
     "AAR_SAK" NUMBER(4,0),
     "LOPENRSAK" NUMBER(7,0),
     "LOPENRVEDTAK" NUMBER,
     "VEDTAK_ID" NUMBER,
     "VEDTAK_ID_RELATERT" NUMBER,
     "VEDTAKTYPEKODE" VARCHAR2(10),
     "STATUS" VARCHAR2(20) DEFAULT 'UBEHANDLET',
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "RENTETILLEGG" NUMBER(8,2),
     "FORELDET_FOR_DATO" DATE,
     "FORELDETBELOP" NUMBER(12,2),
     "STONADTYPE" VARCHAR2(10),
     "REFERANSE" VARCHAR2(20),
     "SKYLDDELINGSGRAD" NUMBER,
     "TILBAKEBETALINGSBELOP" NUMBER(12,2),
     "KJORING_ID" NUMBER,
     "POSTERINGER" CLOB ,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "GRUNNLAGSBELOP_FOR_SKYLDDELING" NUMBER(12,2)
);

COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FEILUTBET_OVERFORING_ID" IS 'Sekvensgenerert ID, Primærnøkkel';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FODSELSNR" IS 'Fødselsnummer';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."AAR_SAK" IS 'Året feilutbetalingssaken i ARENA ble opprettet.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."LOPENRSAK" IS 'Løpenummer for feilutbetalingssaken. ';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."LOPENRVEDTAK" IS 'Løpenummer for tilbakebetalingsvedtak.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAK_ID" IS 'Vedtak Id for feilutbetalingsvedtak';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAK_ID_RELATERT" IS 'Opprinnelig vedtaksidentifikator ved endring, gjenopptak eller stansvedtak.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAKTYPEKODE" IS 'Mulige verdier, O - Ny rettighet, E - Endring, G - Gjenopptak, S -Stans';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."STATUS" IS 'Status';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."DATO_FRA" IS 'Fra dato for tilbakebetalingsvedtak.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."DATO_TIL" IS 'Til dato for tilbakebetalingsvedtak.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."RENTETILLEGG" IS 'Rentetillegg som legges på ved uaktsomhet.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FORELDET_FOR_DATO" IS 'Posteringer før denne dato er foreldet.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FORELDETBELOP" IS 'Totalt foreldet beløp.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."STONADTYPE" IS 'Refererer til vedtakets rettighetkode i ARENA - skal alltid være TILBBET.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REFERANSE" IS 'År, løpenummer sak, løpenummer vedtak.  eks 20220080406001';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."SKYLDDELINGSGRAD" IS 'Skylddelingsgrad angis dersom tilbakebetalingsbeløpet skal reduseres på grunn av fordelt skyld.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."TILBAKEBETALINGSBELOP" IS 'Totalt tilbakebetalingsbeløp uten renter.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."KJORING_ID" IS 'Referer til kjøringsid i batch-loggen gs_logg_detaljer.';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."POSTERINGER" IS 'En collection type, varray med et maks antall rader(1000) med posteringsdetaljer';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REG_DATO" IS 'Angir tidspunktet for når raden ble opprettet';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."MOD_DATO" IS 'Angir tidspunktet for når raden sist ble endret';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."GRUNNLAGSBELOP_FOR_SKYLDDELING" IS 'Grunnlagsbeløp for skylddeling';
COMMENT ON TABLE "FEILUTBET_OVERFORING_HIST"  IS 'Tabell med grunnlagsdata for overføring av feilutbelainger til OS/UR';


--------------------------------------------------------
--  DDL for Table KVOTEBRUK
--------------------------------------------------------

CREATE TABLE "KVOTEBRUK"
(	"KVOTEBRUK_ID" NUMBER,
     "KVOTETYPEKODE" VARCHAR2(5),
     "TABELLNAVNALIAS_GRUNNLAG" VARCHAR2(10),
     "OBJEKT_ID_GRUNNLAG" NUMBER,
     "ANTALL_BEVEGELSE" NUMBER(5,0),
     "POSTERINGTYPEKODE" VARCHAR2(5) DEFAULT 'ORD',
     "REG_USER" VARCHAR2(20),
     "REG_DATO" DATE,
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "DATO_HENDELSE" DATE DEFAULT sysdate,
     "PERSON_ID" NUMBER,
     "BEGRUNNELSE" VARCHAR2(255),
     "PARTISJON" NUMBER(8,0)
)  ;

COMMENT ON COLUMN "KVOTEBRUK"."KVOTEBRUK_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "KVOTEBRUK"."KVOTETYPEKODE" IS 'Referanse til KVOTETYPE';
COMMENT ON COLUMN "KVOTEBRUK"."TABELLNAVNALIAS_GRUNNLAG" IS 'Referanse til OBJEKTTYPE';
COMMENT ON COLUMN "KVOTEBRUK"."OBJEKT_ID_GRUNNLAG" IS 'Objekt id grunnlag';
COMMENT ON COLUMN "KVOTEBRUK"."ANTALL_BEVEGELSE" IS 'Antall bevegelse';
COMMENT ON COLUMN "KVOTEBRUK"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
COMMENT ON COLUMN "KVOTEBRUK"."REG_USER" IS 'Oracle brukerident som opprettet posten';
COMMENT ON COLUMN "KVOTEBRUK"."REG_DATO" IS 'Dato opprettet';
COMMENT ON COLUMN "KVOTEBRUK"."MOD_DATO" IS 'Dato sist modifisert';
COMMENT ON COLUMN "KVOTEBRUK"."MOD_USER" IS 'Oracle brukerident som sist modifiserte posten';
COMMENT ON COLUMN "KVOTEBRUK"."DATO_HENDELSE" IS 'Dato hendelse';
COMMENT ON COLUMN "KVOTEBRUK"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "KVOTEBRUK"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
COMMENT ON COLUMN "KVOTEBRUK"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "KVOTEBRUK"  IS 'Gir oversikt over alle bevegelser for kvotene for en rettighetsperson. Opprettes fra Vedtak, Meldekort og Spesialutbetaling. Genererer Beregningsledd, som bl.a. inneholder oppdatert saldo';


--------------------------------------------------------
--  DDL for Table KVOTEBRUK_DETALJER
--------------------------------------------------------

CREATE TABLE "KVOTEBRUK_DETALJER"
(	"KVOTEBRUK_DETALJER_ID" NUMBER ,
     "KVOTETYPEKODE" VARCHAR2(5),
     "POSTERINGTYPEKODE" VARCHAR2(5),
     "TABELLNAVNALIAS_GRUNNLAG" VARCHAR2(10),
     "OBJEKT_ID_GRUNNLAG" NUMBER,
     "ANTALL_BEVEGELSE" NUMBER,
     "DATO_HENDELSE" DATE DEFAULT SYSDATE,
     "KVOTEBRUK_ID" NUMBER,
     "MELDEKORT_ID" NUMBER,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8)
) ;

COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."KVOTEBRUK_DETALJER_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."KVOTETYPEKODE" IS 'Referanse til KVOTETYPE. Kvotetypen som har gitt bevegelse';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."POSTERINGTYPEKODE" IS 'Referanse til type POSTERINGTYPE';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."TABELLNAVNALIAS_GRUNNLAG" IS 'Referanse til OBJEKTTYPE';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."OBJEKT_ID_GRUNNLAG" IS 'Referanse til objekttypens id';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."ANTALL_BEVEGELSE" IS 'Antall bevegelse i prosent';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."DATO_HENDELSE" IS 'Dato kvote er forbrukt';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."KVOTEBRUK_ID" IS 'Referanse til kvotebruk';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."MELDEKORT_ID" IS 'Referanse til meldekort';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."REG_DATO" IS 'Angir tidspunktet for nÃ¥r raden ble opprettet';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."MOD_DATO" IS 'Angir tidspunktet for nÃ¥r raden sist ble endret';
COMMENT ON COLUMN "KVOTEBRUK_DETALJER"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON TABLE "KVOTEBRUK_DETALJER"  IS 'Gir oversikt over alle bevegelser for kvotene for en rettighetsperson. Opprettes fra Vedtak, Meldekort og Spesialutbetaling. Genererer Beregningsledd, som bl.a. inneholder oppdatert saldo';



--------------------------------------------------------
--  DDL for Table LOGGLINJE
--------------------------------------------------------

CREATE TABLE "LOGGLINJE"
(	"LOGGLINJE_ID" NUMBER,
     "AETATENHET_ANSVARLIG" VARCHAR2(8),
     "HENDELSETYPEKODE" VARCHAR2(7),
     "SAK_ID" NUMBER,
     "SAKSBEHANDLER_ANSVARLIG" VARCHAR2(8),
     "PERSON_ID" NUMBER,
     "OBJEKT_ID_ORIGINAL" NUMBER,
     "KOMMKODE" VARCHAR2(5),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "DATO_HENDELSE" DATE DEFAULT sysdate,
     "TEKST" VARCHAR2(4000),
     "PARTISJON" NUMBER(8,0)
)  ;

COMMENT ON COLUMN "LOGGLINJE"."LOGGLINJE_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "LOGGLINJE"."AETATENHET_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE. Arbeidskontor som er ansvarlig for sak';
COMMENT ON COLUMN "LOGGLINJE"."HENDELSETYPEKODE" IS 'Referanse til HENDELSETYPE';
COMMENT ON COLUMN "LOGGLINJE"."SAK_ID" IS 'Referanse til SAK. Hvilken sak hendelsen gjelder.';
COMMENT ON COLUMN "LOGGLINJE"."SAKSBEHANDLER_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE. Saksbehandler som er ansvarlig for vedtak og sak';
COMMENT ON COLUMN "LOGGLINJE"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "LOGGLINJE"."OBJEKT_ID_ORIGINAL" IS 'Id til kommkode. Sammen med kommkode id til riktig kommunikasjonsform';
COMMENT ON COLUMN "LOGGLINJE"."KOMMKODE" IS 'Referanse til KOMMTYPE';
COMMENT ON COLUMN "LOGGLINJE"."DATO_HENDELSE" IS 'Dato for hendelsen';
COMMENT ON COLUMN "LOGGLINJE"."TEKST" IS 'Brukes til notattekst eller til en definert sammensatt streng for andre hendelsestyper enn notat.';
COMMENT ON COLUMN "LOGGLINJE"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "LOGGLINJE"  IS 'Inneholder linjene som ugjør hendelseslogg.';


--------------------------------------------------------
--  DDL for Table LOV_VEDTAK_SAKSOPPLYSNING
--------------------------------------------------------

CREATE TABLE "LOV_VEDTAK_SAKSOPPLYSNING"
(    "VEDTAK_ID" NUMBER,
     "SAKSOPPLYSNING_ID" NUMBER,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "PARTISJON" NUMBER(8,0),
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE
) ;

COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."SAKSOPPLYSNING_ID" IS 'Referanse til SAKSOPPLYSNING';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."REG_DATO" IS 'Angir tidspunkt for når raden ble opprettet';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON COLUMN "LOV_VEDTAK_SAKSOPPLYSNING"."MOD_DATO" IS 'Angir tidspunkt for når raden sist ble endret';
COMMENT ON TABLE "LOV_VEDTAK_SAKSOPPLYSNING"  IS 'Liste over saksopplysninger som inngår i grunnlaget for et vedtak';



--------------------------------------------------------
--  DDL for Table MELDEKORT
--------------------------------------------------------

CREATE TABLE "MELDEKORT"
(	"MELDEKORT_ID" NUMBER,
     "PERSON_ID" NUMBER,
     "DATO_INNKOMMET" DATE,
     "DATO_UTSENDT" DATE,
     "MKSREFERANSE" VARCHAR2(21),
     "MELDEKORTKODE" VARCHAR2(5),
     "MKSKORTKODE" VARCHAR2(2),
     "STATUS_ARBEIDET" VARCHAR2(1),
     "STATUS_FERIE" VARCHAR2(1) DEFAULT 'N',
     "STATUS_KURS" VARCHAR2(1) DEFAULT NULL,
     "STATUS_NYTT_MELDEKORT" VARCHAR2(1) DEFAULT 'I',
     "STATUS_SYK" VARCHAR2(1) DEFAULT NULL,
     "STATUS_PERIODESPOERSMAAL" VARCHAR2(1) DEFAULT 'N',
     "STATUS_SOEKER_DAGPENGER" VARCHAR2(1),
     "STATUS_ANNETFRAVAER_ATTF" VARCHAR2(1) DEFAULT NULL,
     "STATUS_ATTFORINGSBISTAND" VARCHAR2(1) DEFAULT 'I',
     "STATUS_ATTFORINGSTILTAK" VARCHAR2(1) DEFAULT 'I',
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "AAR" NUMBER(4,0),
     "PERIODEKODE" VARCHAR2(2),
     "BEREGNINGSTATUSKODE" VARCHAR2(5),
     "STATUS_ANNETFRAVAER" VARCHAR2(1),
     "STATUS_FORTSATT_ARBEIDSOKER" VARCHAR2(1),
     "FEIL_PAA_KORT" VARCHAR2(1),
     "VEILEDNING" VARCHAR2(1),
     "KOMMENTAR" VARCHAR2(255),
     "MELDEGRUPPEKODE" VARCHAR2(5),
     "RETURBREVKODE" VARCHAR2(2),
     "AB_POSTKODE" VARCHAR2(1),
     "MELDEKORT_ID_RELATERT" NUMBER,
     "PARTISJON" NUMBER(8,0)
)  ;

COMMENT ON COLUMN "MELDEKORT"."MELDEKORT_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "MELDEKORT"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "MELDEKORT"."DATO_INNKOMMET" IS 'Meldedato. Dato når meldekort har blitt mottatt.';
COMMENT ON COLUMN "MELDEKORT"."DATO_UTSENDT" IS 'Dato for utsending av meldekort';
COMMENT ON COLUMN "MELDEKORT"."MKSREFERANSE" IS 'Meldekortinformasjon. Referanse til et scannet meldekort hos AMELDING (arkivnøkkel) dersom papirkort';
COMMENT ON COLUMN "MELDEKORT"."MELDEKORTKODE" IS 'Referanse til MELDEKORTKODE. Refererer til MELDEKORTPERIODEBRUK sammen med AAR og PERIODEKODE. ''DP'' eller ''AT''';
COMMENT ON COLUMN "MELDEKORT"."MKSKORTKODE" IS 'Referanse til MKSKORTTYPE. Lovlige meldekorttyper (elektronisk, papir, manuelt osv)';
COMMENT ON COLUMN "MELDEKORT"."STATUS_ARBEIDET" IS 'Svar på om bruker har arbeidet i perioden';
COMMENT ON COLUMN "MELDEKORT"."STATUS_FERIE" IS 'Svar på om bruker har hatt ferie i perioden';
COMMENT ON COLUMN "MELDEKORT"."STATUS_KURS" IS 'Svar på om bruker har vært i utdanning/tiltak i perioden';
COMMENT ON COLUMN "MELDEKORT"."STATUS_NYTT_MELDEKORT" IS 'Ikke i bruk.';
COMMENT ON COLUMN "MELDEKORT"."STATUS_SYK" IS 'Svar på om bruker har vært syk i perioden';
COMMENT ON COLUMN "MELDEKORT"."STATUS_PERIODESPOERSMAAL" IS 'Svar på om bruker ønsker forskudd på utbetaling for neste periode';
COMMENT ON COLUMN "MELDEKORT"."STATUS_SOEKER_DAGPENGER" IS 'Ikke i bruk. Historisk fra omlegging av meldekortløsning i 2005. Brukes ikke for visning av gamle meldekort';
COMMENT ON COLUMN "MELDEKORT"."STATUS_ANNETFRAVAER_ATTF" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra før omlegging av meldekortløsning i 2005';
COMMENT ON COLUMN "MELDEKORT"."STATUS_ATTFORINGSBISTAND" IS 'Ikke i bruk. Brukes for visning av gamle meldekort fra før omlegging av meldekortløsning i 2005';
COMMENT ON COLUMN "MELDEKORT"."STATUS_ATTFORINGSTILTAK" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra før omlegging av meldekortløsning i 2005';
COMMENT ON COLUMN "MELDEKORT"."AAR" IS 'År. Refererer til MELDEKORTPERIODEBRUK sammen med periodekode og meldekortkode';
COMMENT ON COLUMN "MELDEKORT"."PERIODEKODE" IS 'Periodenummer med ledene null (01-53). Refererer til MELDEKORTPERIODEBRUK sammen med aar og meldekortkode';
COMMENT ON COLUMN "MELDEKORT"."BEREGNINGSTATUSKODE" IS 'Referanse til BEREGNINGSTATUS. Definerer om meldekort er klar for beregning, ferdig beregnet eller feilaktig, etc.';
COMMENT ON COLUMN "MELDEKORT"."STATUS_ANNETFRAVAER" IS 'Svar på om bruker av andre grunner ikke vært arbeidssøker';
COMMENT ON COLUMN "MELDEKORT"."STATUS_FORTSATT_ARBEIDSOKER" IS 'Ønsker bruker fremdeles å få meldekort tilsendt og stå som arbeidssøker?';
COMMENT ON COLUMN "MELDEKORT"."FEIL_PAA_KORT" IS 'Status J/N indikerer om det er funnet feil på meldekortet';
COMMENT ON COLUMN "MELDEKORT"."VEILEDNING" IS 'Ikke i bruk. Historisk fra omlegging av meldekortløsning i 2005. Brukes ikke for visning av gamle meldekort';
COMMENT ON COLUMN "MELDEKORT"."KOMMENTAR" IS 'Saksbehandlers kommentar';
COMMENT ON COLUMN "MELDEKORT"."MELDEGRUPPEKODE" IS 'Referanse til MELDEGRUPPETYPE';
COMMENT ON COLUMN "MELDEKORT"."RETURBREVKODE" IS 'Returkode dersom meldekort returneres til bruker. MKS_BREVTYPE inneholder returkoder. Kan inneholde blank dersom ingen retur';
COMMENT ON COLUMN "MELDEKORT"."AB_POSTKODE" IS 'Sende kortet som A, B eller C-post.';
COMMENT ON COLUMN "MELDEKORT"."MELDEKORT_ID_RELATERT" IS 'Relasjon til opprinnelig meldekort';
COMMENT ON COLUMN "MELDEKORT"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "MELDEKORT"  IS 'Meldekort for dagpenger og AAP.  Tabellen inneholder meldekort for personer som går på Dagpenger og AAP. Meldekortet gjelder for en 2-ukers periode. Innholdet pr dag finnes i tabellen MELDEKORTDAG.Meldekort kan registrers i Arena av saksbehandler, men de fleste leveres elektronisk.';


--------------------------------------------------------
--  DDL for Table MELDEKORTDAG
--------------------------------------------------------

CREATE TABLE "MELDEKORTDAG"
(	"MELDEKORT_ID" NUMBER,
     "UKENR" NUMBER(2,0),
     "DAGNR" NUMBER(1,0),
     "STATUS_ARBEIDSDAG" VARCHAR2(1),
     "STATUS_FERIE" VARCHAR2(1),
     "STATUS_KURS" VARCHAR2(1),
     "STATUS_SYK" VARCHAR2(1),
     "STATUS_ANNETFRAVAER_ATTF" VARCHAR2(1),
     "TIMER_ARBEIDET" NUMBER(3,1) DEFAULT 0,
     "TIMER_ARB_MENS_PERM" NUMBER(3,1) DEFAULT 0,
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "STATUS_ANNETFRAVAER" VARCHAR2(1),
     "MELDEGRUPPEKODE" VARCHAR2(5),
     "PARTISJON" NUMBER(8,0)
)  ;

COMMENT ON COLUMN "MELDEKORTDAG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT';
COMMENT ON COLUMN "MELDEKORTDAG"."UKENR" IS 'Ukenr mellom 01 og 53';
COMMENT ON COLUMN "MELDEKORTDAG"."DAGNR" IS 'Dagnr mellom 1 og 7';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ARBEIDSDAG" IS 'Satt hvis TIMER_ARBEIDET > 0';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_FERIE" IS 'Svar på om bruker har hatt ferie';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_KURS" IS 'Svar på om bruker har vært i utdanning/tiltak';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_SYK" IS 'Svar på om bruker har vært syk';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ANNETFRAVAER_ATTF" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra før omlegging av meldekortløsning i 2005';
COMMENT ON COLUMN "MELDEKORTDAG"."TIMER_ARBEIDET" IS 'Totalt antall timer arbeidet på dagen (egne og annen arbeidsgiver).';
COMMENT ON COLUMN "MELDEKORTDAG"."TIMER_ARB_MENS_PERM" IS 'Ikke i bruk. Antall timer en permittert person har arbeidet hos annen arbeidsgiver.';
COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ANNETFRAVAER" IS 'Svar på om bruker av andre grunner ikke vært arbeidssøker';
COMMENT ON COLUMN "MELDEKORTDAG"."MELDEGRUPPEKODE" IS 'Referanse til MELDEGRUPPETYPE';
COMMENT ON COLUMN "MELDEKORTDAG"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "MELDEKORTDAG"  IS 'Opplysninger for et meldekort pr dag i en uke i en meldekortperiode.Det vil ligge opplysninger om hvor mange timer/dager som bruker har arbeidet, vært sy, har annet fravær osv.';



--------------------------------------------------------
--  DDL for Table MELDELOGG
--------------------------------------------------------

CREATE TABLE "MELDELOGG"
(	"MELDEKORT_ID" NUMBER,
     "HENDELSEDATO" DATE,
     "HENDELSETYPEKODE" VARCHAR2(7),
     "LOGGTEKST" VARCHAR2(255),
     "KORRELASJONS_ID" VARCHAR2(30),
     "REG_USER" VARCHAR2(8),
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "MELDELOGG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT';
COMMENT ON COLUMN "MELDELOGG"."HENDELSEDATO" IS 'Dato for hendelse';
COMMENT ON COLUMN "MELDELOGG"."HENDELSETYPEKODE" IS 'Referanse til hendelsetype (Opprettet, Sendt osv)';
COMMENT ON COLUMN "MELDELOGG"."LOGGTEKST" IS 'Fritekst beskrivelse av hendelsen';
COMMENT ON COLUMN "MELDELOGG"."KORRELASJONS_ID" IS 'Flere meldinger for samme person sendes som en SOAP-melding med samme korrelasjons_id. Referanse til AOL_SOAPKALL_LOGG.';
COMMENT ON COLUMN "MELDELOGG"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "MELDELOGG"  IS 'Inneholder historikk for hendelser på meldekort';



--------------------------------------------------------
--  DDL for Table ORGUNITINSTANCE
--------------------------------------------------------

CREATE TABLE "ORGUNITINSTANCE"
(	"ID" VARCHAR2(100),
     "TYPE" VARCHAR2(1),
     "FIRSTNAME" VARCHAR2(50),
     "LASTNAME" VARCHAR2(50),
     "NAME" VARCHAR2(50),
     "INITIALS" VARCHAR2(50),
     "CONTACTSTRING" VARCHAR2(240),
     "USERNAME" VARCHAR2(20),
     "MERGED_WITH" VARCHAR2(20),
     "MERGED_DATE" DATE,
     "CURRENT_USERNAME" VARCHAR2(20),
     "FYLKESNR" NUMBER(2,0),
     "SPRAAKKODE" VARCHAR2(2) DEFAULT 'NO',
     "KILDE" VARCHAR2(10) DEFAULT 'ARENA',
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "ORGNR" NUMBER(11,0)
) ;

COMMENT ON COLUMN "ORGUNITINSTANCE"."KILDE" IS 'Angir hvilken kilde som vedlikeholder organisasjonsenheten';
COMMENT ON COLUMN "ORGUNITINSTANCE"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
COMMENT ON COLUMN "ORGUNITINSTANCE"."REG_DATO" IS 'Angir tidspunkt for når raden ble opprettet';
COMMENT ON COLUMN "ORGUNITINSTANCE"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
COMMENT ON COLUMN "ORGUNITINSTANCE"."MOD_DATO" IS 'Angir tidspunkt for når raden sist ble endret';
COMMENT ON COLUMN "ORGUNITINSTANCE"."ORGNR" IS 'Knytter et kontor til et organsisasjonsnummer. Kan være fiktivt.';
COMMENT ON TABLE "ORGUNITINSTANCE"  IS 'FrameSolution-rammeverk tabell. Inneholder informasjon om organisasjonselementer, dvs. kontorer og personer';


--------------------------------------------------------
--  DDL for Table PERSONFORHOLD
--------------------------------------------------------

CREATE TABLE "PERSONFORHOLD"
(	"PERSON_ID" NUMBER,
     "PERSONFORHOLDKODE" VARCHAR2(5),
     "KOMMENTAR" VARCHAR2(4000),
     "DATO_SLUTT" DATE DEFAULT to_date('20032099','DDMMYYYY'),
     "REG_USER" VARCHAR2(8),
     "REG_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "DATO_START" DATE DEFAULT trunc(sysdate),
     "PROFILELEMENT_ID" NUMBER,
     "LEDDKODE" VARCHAR2(5)
) ;

COMMENT ON COLUMN "PERSONFORHOLD"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "PERSONFORHOLD"."PERSONFORHOLDKODE" IS 'Referanse til PERSONFORHOLDTYPE.';
COMMENT ON COLUMN "PERSONFORHOLD"."KOMMENTAR" IS 'Saksbehandlers kommentar til den registrerte posten';
COMMENT ON COLUMN "PERSONFORHOLD"."DATO_SLUTT" IS 'Når det aktuelle personforholdet ble avsluttet';
COMMENT ON COLUMN "PERSONFORHOLD"."DATO_START" IS 'Når det aktuelle personforholdet ble startet';
COMMENT ON COLUMN "PERSONFORHOLD"."PROFILELEMENT_ID" IS 'Referanse til PROFILELEMENT';
COMMENT ON COLUMN "PERSONFORHOLD"."LEDDKODE" IS 'Referanse til LEDDTYPE';
COMMENT ON TABLE "PERSONFORHOLD"  IS 'Tabellen inneholder ulike personkarakteristika gitt ved personforholdtype.';


CREATE TABLE "POSTERING"
(	"POSTERING_ID" NUMBER,
     "BELOP" NUMBER(12,2),
     "BELOPKODE" VARCHAR2(5),
     "DATO_PERIODE_FRA" DATE,
     "DATO_PERIODE_TIL" DATE,
     "DATO_POSTERT" DATE,
     "EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
     "AAR" NUMBER(4,0),
     "PERSON_ID" NUMBER,
     "POSTERINGSATS" NUMBER(8,2),
     "POSTERINGTYPEKODE" VARCHAR2(5),
     "TRANSAKSJONSKODE" VARCHAR2(5),
     "ANTALL" NUMBER(14,4),
     "MELDINGKODE" VARCHAR2(10),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "DATO_GRUNNLAG" DATE,
     "VEDTAK_ID" NUMBER,
     "ARTKODE" VARCHAR2(5),
     "PROSJEKTNUMMER" VARCHAR2(4),
     "KAPITTEL" VARCHAR2(4),
     "POST" VARCHAR2(2),
     "UNDERPOST" VARCHAR2(3),
     "KONTOSTEDKODE" VARCHAR2(5),
     "MELDEKORT_ID" NUMBER,
     "TRANSAKSJONSTEKST" VARCHAR2(60),
     "BRUKER_ID_SAKSBEHANDLER" VARCHAR2(8),
     "AETATENHET_ANSVARLIG" VARCHAR2(8),
     "TABELLNAVNALIAS_KILDE" VARCHAR2(10),
     "OBJEKT_ID_KILDE" NUMBER(20,0),
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "POSTERING"."POSTERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "POSTERING"."BELOP" IS 'Beløp';
COMMENT ON COLUMN "POSTERING"."BELOPKODE" IS 'Referanse til BELOPTYPE';
COMMENT ON COLUMN "POSTERING"."DATO_PERIODE_FRA" IS 'Dato periode fra';
COMMENT ON COLUMN "POSTERING"."DATO_PERIODE_TIL" IS 'Dato periode til';
COMMENT ON COLUMN "POSTERING"."DATO_POSTERT" IS 'Dato postert';
COMMENT ON COLUMN "POSTERING"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til BETALINGMOTTAKER';
COMMENT ON COLUMN "POSTERING"."AAR" IS 'År';
COMMENT ON COLUMN "POSTERING"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "POSTERING"."POSTERINGSATS" IS 'Posteringsats';
COMMENT ON COLUMN "POSTERING"."POSTERINGTYPEKODE" IS 'Kode som entydig identifiserer posteringstype';
COMMENT ON COLUMN "POSTERING"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
COMMENT ON COLUMN "POSTERING"."ANTALL" IS 'Antall dager';
COMMENT ON COLUMN "POSTERING"."MELDINGKODE" IS 'Referanse til MELDINGTYPE';
COMMENT ON COLUMN "POSTERING"."REG_DATO" IS 'Dato opprettet';
COMMENT ON COLUMN "POSTERING"."REG_USER" IS 'Oracle brukerident som opprettet posten';
COMMENT ON COLUMN "POSTERING"."MOD_DATO" IS 'Dato sist modifisert';
COMMENT ON COLUMN "POSTERING"."MOD_USER" IS 'Oracle brukerident som sist modifiserte posten';
COMMENT ON COLUMN "POSTERING"."DATO_GRUNNLAG" IS 'Dato grunnlag';
COMMENT ON COLUMN "POSTERING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "POSTERING"."ARTKODE" IS 'Referanse til ART. Konteringsart';
COMMENT ON COLUMN "POSTERING"."PROSJEKTNUMMER" IS 'Ikke i bruk';
COMMENT ON COLUMN "POSTERING"."KAPITTEL" IS 'Kapittel i statsregnskapet';
COMMENT ON COLUMN "POSTERING"."POST" IS 'Angir post i statsregnskapet';
COMMENT ON COLUMN "POSTERING"."UNDERPOST" IS 'Angir underpost i statsregnskapet';
COMMENT ON COLUMN "POSTERING"."KONTOSTEDKODE" IS 'Referanse til KONTOSTED. Kontosted for en kontering (kontostreng + sted+aar)';
COMMENT ON COLUMN "POSTERING"."MELDEKORT_ID" IS 'Referanse til MELDEKORT. Inkludert av hensyn til utbetalings/posteringshistorikk med referanse til anmerkninger som refererer til meldekort.';
COMMENT ON COLUMN "POSTERING"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
COMMENT ON COLUMN "POSTERING"."BRUKER_ID_SAKSBEHANDLER" IS 'ID til ORACLE-brukerident som har lagt inn postering';
COMMENT ON COLUMN "POSTERING"."AETATENHET_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE';
COMMENT ON COLUMN "POSTERING"."TABELLNAVNALIAS_KILDE" IS 'Peker på tabellen som er opphav til utbetalingen';
COMMENT ON COLUMN "POSTERING"."OBJEKT_ID_KILDE" IS 'Peker på en forekomst i tabellen angitt i Tabellnavnalias_kilde  som er opphav til utbetalingen';
COMMENT ON COLUMN "POSTERING"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "POSTERING"  IS 'Alle posteringer som er sendt til forsystemet';



--------------------------------------------------------
--  DDL for Table SAKSOPPLYSNING
--------------------------------------------------------

CREATE TABLE "SAKSOPPLYSNING"
(	"SAKSOPPLYSNING_ID" NUMBER,
     "SAK_ID" NUMBER,
     "SAKSOPPLYSNINGKODE" VARCHAR2(10),
     "VERDI" VARCHAR2(2000),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "STATUS_FROSSET" VARCHAR2(1) DEFAULT 'N',
     "PARTISJON" NUMBER(8,0)
)  ;

COMMENT ON COLUMN "SAKSOPPLYSNING"."SAKSOPPLYSNING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "SAKSOPPLYSNING"."SAK_ID" IS 'Referanse til SAK';
COMMENT ON COLUMN "SAKSOPPLYSNING"."SAKSOPPLYSNINGKODE" IS 'Referanse til SAKSOPPLYSNINGTYPE';
COMMENT ON COLUMN "SAKSOPPLYSNING"."VERDI" IS 'Verdi, innhold i saksopplysningen';
COMMENT ON COLUMN "SAKSOPPLYSNING"."STATUS_FROSSET" IS 'Status frosset. J = kan ikke endres. N = kan endres.';
COMMENT ON COLUMN "SAKSOPPLYSNING"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "SAKSOPPLYSNING"  IS 'Saksopplysning i en aktuell sak';


--------------------------------------------------------
--  DDL for Table SAKSRELASJON
--------------------------------------------------------

CREATE TABLE "SAKSRELASJON"
(	"SAKSRELASJON_ID" NUMBER,
     "RELASJONSKODE" VARCHAR2(5),
     "SAK_ID" NUMBER,
     "EKSTERNENHET_ID" NUMBER,
     "EKSTERNSAKBETEGNELSE" VARCHAR2(30),
     "SAK_ID_INTERNRELATERT" NUMBER,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "SAKSRELASJON"."SAKSRELASJON_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "SAKSRELASJON"."RELASJONSKODE" IS 'Referanse til RELASJONSTYPE';
COMMENT ON COLUMN "SAKSRELASJON"."SAK_ID" IS 'Referanse til SAK';
COMMENT ON COLUMN "SAKSRELASJON"."EKSTERNENHET_ID" IS 'Referanse til EKSTERNENHET';
COMMENT ON COLUMN "SAKSRELASJON"."EKSTERNSAKBETEGNELSE" IS 'Hva saken heter hos ekstern enhet';
COMMENT ON COLUMN "SAKSRELASJON"."SAK_ID_INTERNRELATERT" IS 'Brukes hvis det er relasjon til en intern sak, f. eks ved flytting';
COMMENT ON COLUMN "SAKSRELASJON"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "SAKSRELASJON"  IS 'Hvilke relasjoner som finnes mellom saker';


--------------------------------------------------------
--  DDL for Table SPESIALUTBETALING
--------------------------------------------------------

CREATE TABLE "SPESIALUTBETALING"
(	"SPESUTBETALING_ID" NUMBER,
     "PERSON_ID" NUMBER,
     "VEDTAK_ID" NUMBER,
     "LOPENR" NUMBER(3,0),
     "BRUKER_ID_SAKSBEHANDLER" VARCHAR2(8),
     "BRUKER_ID_BESLUTTER" VARCHAR2(8),
     "DATO_UTBETALING" DATE,
     "BEGRUNNELSE" VARCHAR2(2000),
     "BELOP" NUMBER(12,2),
     "BELOPKODE" VARCHAR2(5),
     "RETTIGHETKODE" VARCHAR2(10),
     "AKTFASEKODE" VARCHAR2(10),
     "VEDTAKSTATUSKODE" VARCHAR2(5),
     "POSTERINGTYPEKODE" VARCHAR2(5),
     "REFERANSE_TOTAL" VARCHAR2(255),
     "DATO_FRA" DATE,
     "DATO_TIL" DATE,
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
     "FERIEGRUNNLAG" NUMBER(12,2),
     "FERIEGRUNNLAGKODE" VARCHAR2(10),
     "ORDINAER_YTELSE" VARCHAR2(1),
     "REFERANSE_BILAG" VARCHAR2(25),
     "STATUS_BILAG" VARCHAR2(1),
     "STATUS_ANVIS_BILAG" VARCHAR2(1),
     "PARTISJON" NUMBER(8,0),
     "VALGT_UTBET_TYPE" VARCHAR2(20),
     "KATEGORI" VARCHAR2(50)
) ;

COMMENT ON COLUMN "SPESIALUTBETALING"."SPESUTBETALING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "SPESIALUTBETALING"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "SPESIALUTBETALING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "SPESIALUTBETALING"."LOPENR" IS 'Et generert nummer for å angi rekkefølge på spesialutbealingen., skal være unik innenfor et vedtak';
COMMENT ON COLUMN "SPESIALUTBETALING"."BRUKER_ID_SAKSBEHANDLER" IS 'Referanse til ORGUNITINSTANCE';
COMMENT ON COLUMN "SPESIALUTBETALING"."BRUKER_ID_BESLUTTER" IS 'Referanse til ORGUNITINSTANCE';
COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_UTBETALING" IS 'Dato utbetaling';
COMMENT ON COLUMN "SPESIALUTBETALING"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
COMMENT ON COLUMN "SPESIALUTBETALING"."BELOP" IS 'Beløp';
COMMENT ON COLUMN "SPESIALUTBETALING"."BELOPKODE" IS 'Referanse til KONTOTYPE';
COMMENT ON COLUMN "SPESIALUTBETALING"."RETTIGHETKODE" IS 'Referanse til LOV_RETTIGHETTYPE_AKTFAS';
COMMENT ON COLUMN "SPESIALUTBETALING"."AKTFASEKODE" IS 'Referanse til LOV_RETTIGHETTYPE_AKTFAS';
COMMENT ON COLUMN "SPESIALUTBETALING"."VEDTAKSTATUSKODE" IS 'Referanse til VEDTAKSTATUS';
COMMENT ON COLUMN "SPESIALUTBETALING"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
COMMENT ON COLUMN "SPESIALUTBETALING"."REFERANSE_TOTAL" IS 'Tekstlig referanse til tidligere vedtak iTotal som ikke er konvertert.';
COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
COMMENT ON COLUMN "SPESIALUTBETALING"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til EKSTERNENHET';
COMMENT ON COLUMN "SPESIALUTBETALING"."FERIEGRUNNLAG" IS 'Feriegrunnlag. Beløpet som rapporteres til forsystemet som grunnlag for utbetaling av feriepenger';
COMMENT ON COLUMN "SPESIALUTBETALING"."FERIEGRUNNLAGKODE" IS 'Referanse til FERIEGRUNNLAGTYPE';
COMMENT ON COLUMN "SPESIALUTBETALING"."ORDINAER_YTELSE" IS 'J hvis utbetalingen gjelder en ordinær ytelse';
COMMENT ON COLUMN "SPESIALUTBETALING"."REFERANSE_BILAG" IS 'Referanse til eventuelle bilag';
COMMENT ON COLUMN "SPESIALUTBETALING"."STATUS_BILAG" IS 'Status bilag J/N';
COMMENT ON COLUMN "SPESIALUTBETALING"."STATUS_ANVIS_BILAG" IS 'Status anviste bilag J/N';
COMMENT ON COLUMN "SPESIALUTBETALING"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON COLUMN "SPESIALUTBETALING"."VALGT_UTBET_TYPE" IS 'Type ventebetingelse som velges fra ny saksopplysning og attributt UTBETVENT / UTBET ( utbetaling på vent ). Gyldige verdier: REFKRAVSOS	Refusjonskrav sosialhjelp, REFKRAVTP	Refusjonskrav tjenestepensjonsordning, AVREGNAYT	Avregning andre folketrygdytelser';
COMMENT ON COLUMN "SPESIALUTBETALING"."KATEGORI" IS 'Angir kategori for spesialutbetaling. Kan benyttes for å merke utbetaling som krever spesiell behandling. ';
COMMENT ON TABLE "SPESIALUTBETALING"  IS 'Spesialutbetalinger for attføringspenger, ordinære dagpenger, samt personer med rett til ferietillegg og reisetillegg';


--------------------------------------------------------
--  DDL for Table UTBETALINGSGRUNNLAG
--------------------------------------------------------

CREATE TABLE "UTBETALINGSGRUNNLAG"
(	"POSTERING_ID" NUMBER,
     "BELOP" NUMBER(12,2),
     "BELOPKODE" VARCHAR2(5),
     "EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
     "AAR" NUMBER(4,0),
     "DATO_PERIODE_FRA" DATE,
     "PERSON_ID" NUMBER,
     "POSTERINGTYPEKODE" VARCHAR2(5),
     "TRANSAKSJONSKODE" VARCHAR2(5),
     "ANTALL" NUMBER(14,4),
     "MELDINGKODE" VARCHAR2(10),
     "POSTERINGSATS" NUMBER(8,2),
     "REG_DATO" DATE,
     "REG_USER" VARCHAR2(8),
     "MOD_DATO" DATE,
     "MOD_USER" VARCHAR2(8),
     "VEDTAK_ID" NUMBER,
     "DATO_GRUNNLAG" DATE,
     "ARTKODE" VARCHAR2(5),
     "PROSJEKTNUMMER" VARCHAR2(4),
     "DATO_PERIODE_TIL" DATE,
     "KAPITTEL" VARCHAR2(4),
     "POST" VARCHAR2(2),
     "UNDERPOST" VARCHAR2(3),
     "KONTOSTEDKODE" VARCHAR2(5),
     "MELDEKORT_ID" NUMBER,
     "TRANSAKSJONSTEKST" VARCHAR2(60),
     "TABELLNAVNALIAS_KILDE" VARCHAR2(10),
     "OBJEKT_ID_KILDE" NUMBER(20,0),
     "PARTISJON" NUMBER(8,0)
) ;

COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."BELOP" IS 'Beløp';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."BELOPKODE" IS 'Referanse til BELOPTYPE';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til BETALINGMOTTAKER, alternativ mottaker';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."AAR" IS 'År';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_PERIODE_FRA" IS 'Dato periode fra';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PERSON_ID" IS 'Referanse til PERSON';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."ANTALL" IS 'Antall';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."MELDINGKODE" IS 'Referanse til MELDINGTYPE';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERINGSATS" IS 'Posteringsats';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_GRUNNLAG" IS 'Dato grunnlag';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."ARTKODE" IS 'Referanse til TILTAKSPROFIL';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PROSJEKTNUMMER" IS 'Prosjektnummer';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_PERIODE_TIL" IS 'Dato periode til';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."KAPITTEL" IS 'Kapittel i statsregnskapet';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POST" IS 'Angir post i statsregnskapet';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."UNDERPOST" IS 'Angir underpost i statsregnskapet';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."KONTOSTEDKODE" IS 'Referanse til KONTOSTED. Kontosted for en kontering (kontostreng + sted+aar)';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT. inkludert av hensyn til utbetalings/posteringshistorikk med referanse til anmerkninger som refererer til meldekort.';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TABELLNAVNALIAS_KILDE" IS 'Tabellnavnalias for kilde til utbetalingen';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."OBJEKT_ID_KILDE" IS 'Peker på en forekomst i tabellen angitt i Tabellnavnalias_kilde  som er opphav til utbetalingen';
COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PARTISJON" IS 'Partisjonsnøkkel';
COMMENT ON TABLE "UTBETALINGSGRUNNLAG"  IS 'Alle utbetalinger som tilrettelegges. Gjøres om til posteringer, når de blir sendt.';



