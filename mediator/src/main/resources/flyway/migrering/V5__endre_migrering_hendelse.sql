alter table migrering_hendelse
    alter column lest_dato drop not null;

alter table migrering_hendelse
    alter column lest_dato drop default;

alter table migrering_hendelse
    add sent_dato timestamp with time zone;
