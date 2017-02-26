# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table csvconnection (
  connection_id                 serial not null,
  connection_name               varchar(255),
  connection_path               varchar(255),
  connection_description        varchar(255),
  delimiter                     varchar(255),
  quote_character               varchar(255),
  newline_character             varchar(255),
  header                        boolean,
  constraint pk_csvconnection primary key (connection_id)
);

create table field (
  id                            bigserial not null,
  field_name                    varchar(255),
  field_type                    varchar(8),
  table_name                    varchar(255),
  pivot_table_id                bigint,
  constraint ck_field_field_type check ( field_type in ('String','Boolean','Integer','Number','Date','Time','DateTime','Double')),
  constraint pk_field primary key (id)
);

create table filter (
  id                            bigserial not null,
  field_id                      bigint,
  pivot_table_id                bigint,
  constraint pk_filter primary key (id)
);

create table filter_valid_value (
  id                            bigserial not null,
  filter_id                     bigint,
  value_type                    varchar(255),
  specific_value                varchar(255),
  start_date                    timestamptz,
  end_date                      timestamptz,
  min_value                     bigint,
  max_value                     bigint,
  constraint pk_filter_valid_value primary key (id)
);

create table pivot_column (
  id                            bigserial not null,
  field_id                      bigint,
  pivot_table_id                bigint,
  constraint pk_pivot_column primary key (id)
);

create table pivot_page (
  id                            bigserial not null,
  field_id                      bigint,
  pivot_table_id                bigint,
  constraint pk_pivot_page primary key (id)
);

create table pivot_row (
  id                            bigserial not null,
  field_id                      bigint,
  pivot_table_id                bigint,
  constraint pk_pivot_row primary key (id)
);

create table pivot_table (
  id                            bigserial not null,
  constraint pk_pivot_table primary key (id)
);

create table pivot_value (
  id                            bigserial not null,
  field_id                      bigint,
  pivot_table_id                bigint,
  pivot_value_type_id           bigint,
  constraint pk_pivot_value primary key (id)
);

create table pivot_value_type (
  id                            bigserial not null,
  value_type                    varchar(255),
  display_name                  varchar(255),
  constraint pk_pivot_value_type primary key (id)
);

create table sqlconnection (
  id                            serial not null,
  connection_driver             varchar(255),
  connection_name               varchar(255),
  connection_description        varchar(255),
  connection_host               varchar(255),
  connection_port               integer,
  connection_user               varchar(255),
  connection_password           varchar(255),
  connection_dbname             varchar(255),
  constraint pk_sqlconnection primary key (id)
);

create table user_list (
  id                            bigserial not null,
  email                         varchar(255),
  full_name                     varchar(255),
  password_hash                 varchar(255),
  last_login                    timestamptz,
  confirmation_token            varchar(255),
  date_creation                 timestamptz not null,
  constraint uq_user_list_email unique (email),
  constraint pk_user_list primary key (id)
);

create table sql_source (
  source_id                     serial not null,
  source_name                   varchar(255),
  sqlconnection_id              integer,
  fact_table                    varchar(255),
  from_clause                   varchar(255),
  constraint pk_sql_source primary key (source_id)
);

alter table field add constraint fk_field_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_field_pivot_table_id on field (pivot_table_id);

alter table filter add constraint fk_filter_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_filter_field_id on filter (field_id);

alter table filter add constraint fk_filter_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_filter_pivot_table_id on filter (pivot_table_id);

alter table filter_valid_value add constraint fk_filter_valid_value_filter_id foreign key (filter_id) references filter (id) on delete restrict on update restrict;
create index ix_filter_valid_value_filter_id on filter_valid_value (filter_id);

alter table pivot_column add constraint fk_pivot_column_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_pivot_column_field_id on pivot_column (field_id);

alter table pivot_column add constraint fk_pivot_column_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_pivot_column_pivot_table_id on pivot_column (pivot_table_id);

alter table pivot_page add constraint fk_pivot_page_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_pivot_page_field_id on pivot_page (field_id);

alter table pivot_page add constraint fk_pivot_page_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_pivot_page_pivot_table_id on pivot_page (pivot_table_id);

alter table pivot_row add constraint fk_pivot_row_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_pivot_row_field_id on pivot_row (field_id);

alter table pivot_row add constraint fk_pivot_row_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_pivot_row_pivot_table_id on pivot_row (pivot_table_id);

alter table pivot_value add constraint fk_pivot_value_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_pivot_value_field_id on pivot_value (field_id);

alter table pivot_value add constraint fk_pivot_value_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_pivot_value_pivot_table_id on pivot_value (pivot_table_id);

alter table pivot_value add constraint fk_pivot_value_pivot_value_type_id foreign key (pivot_value_type_id) references pivot_value_type (id) on delete restrict on update restrict;
create index ix_pivot_value_pivot_value_type_id on pivot_value (pivot_value_type_id);

alter table sql_source add constraint fk_sql_source_sqlconnection_id foreign key (sqlconnection_id) references sqlconnection (id) on delete restrict on update restrict;
create index ix_sql_source_sqlconnection_id on sql_source (sqlconnection_id);


# --- !Downs

alter table if exists field drop constraint if exists fk_field_pivot_table_id;
drop index if exists ix_field_pivot_table_id;

alter table if exists filter drop constraint if exists fk_filter_field_id;
drop index if exists ix_filter_field_id;

alter table if exists filter drop constraint if exists fk_filter_pivot_table_id;
drop index if exists ix_filter_pivot_table_id;

alter table if exists filter_valid_value drop constraint if exists fk_filter_valid_value_filter_id;
drop index if exists ix_filter_valid_value_filter_id;

alter table if exists pivot_column drop constraint if exists fk_pivot_column_field_id;
drop index if exists ix_pivot_column_field_id;

alter table if exists pivot_column drop constraint if exists fk_pivot_column_pivot_table_id;
drop index if exists ix_pivot_column_pivot_table_id;

alter table if exists pivot_page drop constraint if exists fk_pivot_page_field_id;
drop index if exists ix_pivot_page_field_id;

alter table if exists pivot_page drop constraint if exists fk_pivot_page_pivot_table_id;
drop index if exists ix_pivot_page_pivot_table_id;

alter table if exists pivot_row drop constraint if exists fk_pivot_row_field_id;
drop index if exists ix_pivot_row_field_id;

alter table if exists pivot_row drop constraint if exists fk_pivot_row_pivot_table_id;
drop index if exists ix_pivot_row_pivot_table_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_field_id;
drop index if exists ix_pivot_value_field_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_pivot_table_id;
drop index if exists ix_pivot_value_pivot_table_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_pivot_value_type_id;
drop index if exists ix_pivot_value_pivot_value_type_id;

alter table if exists sql_source drop constraint if exists fk_sql_source_sqlconnection_id;
drop index if exists ix_sql_source_sqlconnection_id;

drop table if exists csvconnection cascade;

drop table if exists field cascade;

drop table if exists filter cascade;

drop table if exists filter_valid_value cascade;

drop table if exists pivot_column cascade;

drop table if exists pivot_page cascade;

drop table if exists pivot_row cascade;

drop table if exists pivot_table cascade;

drop table if exists pivot_value cascade;

drop table if exists pivot_value_type cascade;

drop table if exists sqlconnection cascade;

drop table if exists user_list cascade;

drop table if exists sql_source cascade;

