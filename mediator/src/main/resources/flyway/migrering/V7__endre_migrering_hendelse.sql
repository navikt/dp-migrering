

alter table migrering_hendelse
    rename column melding_type to hendelse;

alter table migrering_hendelse
    rename column lest_dato to tidspunkt;

alter table migrering_hendelse
    drop column sent_dato;

alter table migrering_hendelse
    add column opprinnelse TEXT NOT NULL DEFAULT 'UKJENT';