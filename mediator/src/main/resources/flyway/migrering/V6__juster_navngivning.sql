alter table migrering_opplysninger
    rename column opplysninger_id to opplysning_id;

alter table migrering
    rename column migrerings_tidspunkt to migrering_tidspunkt;

