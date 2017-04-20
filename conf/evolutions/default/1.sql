# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table csvconnection (
  id                            bigserial not null,
  connect_name                  varchar(255),
  connection_path               varchar(255),
  connect_description           varchar(255),
  constraint uq_csvconnection_connect_name unique (connect_name),
  constraint pk_csvconnection primary key (id)
);

create table csvsource (
  id                            bigserial not null,
  source_name                   varchar(255),
  source_description            varchar(255),
  csvconnection_id              bigint,
  constraint pk_csvsource primary key (id)
);

create table csvsource_link (
  id                            bigserial not null,
  fact_field                    varchar(255),
  dimension_field               varchar(255),
  csvconnection_id              bigint,
  constraint pk_csvsource_link primary key (id)
);

create table csvsource_link_csvsource (
  csvsource_link_id             bigint not null,
  csvsource_id                  bigint not null,
  constraint pk_csvsource_link_csvsource primary key (csvsource_link_id,csvsource_id)
);

create table column_metadata (
  column_id                     bigserial not null,
  tablemetadata_id              bigint,
  column_name                   varchar(255),
  column_type                   varchar(255),
  column_alias                  varchar(255),
  constraint pk_column_metadata primary key (column_id)
);

create table field (
  id                            bigserial not null,
  field_name                    varchar(255),
  field_type                    varchar(8),
  table_name                    varchar(255),
  pivot_table_id                bigint,
  constraint ck_field_field_type check ( field_type in ('String','Boolean','Long','Date','Time','DateTime','Double')),
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
  user_id                       bigint,
  name                          varchar(255),
  description                   varchar(255),
  sqlsource_id                  bigint,
  csvsource_id                  bigint,
  constraint uq_pivot_table_name unique (name),
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
  id                            bigserial not null,
  connection_driver             varchar(255),
  connection_name               varchar(255),
  connection_description        varchar(255),
  connection_host               varchar(255),
  connection_port               integer,
  connection_user               varchar(255),
  connection_password           varchar(255),
  connection_dbname             varchar(255),
  constraint uq_sqlconnection_connection_name unique (connection_name),
  constraint pk_sqlconnection primary key (id)
);

create table sqldimension (
  id                            bigserial not null,
  dimension_table               varchar(255),
  fact_field                    varchar(255),
  dimension_field               varchar(255),
  sqlsource_id                  bigint,
  constraint pk_sqldimension primary key (id)
);

create table sqlsource (
  id                            bigserial not null,
  source_name                   varchar(255),
  source_description            varchar(255),
  sqlconnection_id              bigint,
  fact_table                    varchar(255),
  from_clause                   varchar(255),
  constraint pk_sqlsource primary key (id)
);

create table share_permission (
  id                            bigserial not null,
  user_id                       bigint,
  permission                    varchar(255),
  pivot_table_id                bigint,
  constraint pk_share_permission primary key (id)
);

create table table_metadata (
  id                            bigserial not null,
  schema_name                   varchar(255),
  table_name                    varchar(255),
  sqlconnection_id              bigint,
  constraint pk_table_metadata primary key (id)
);

create table token (
  token                         varchar(255) not null,
  user_id                       bigint,
  type                          varchar(8),
  date_creation                 timestamptz,
  constraint ck_token_type check ( type in ('PASSWORD','EMAIL','NEWUSER')),
  constraint pk_token primary key (token)
);

create table user_list (
  id                            bigserial not null,
  email                         varchar(255),
  full_name                     varchar(255),
  password_hash                 varchar(255),
  confirmation_token            varchar(255),
  constraint uq_user_list_email unique (email),
  constraint pk_user_list primary key (id)
);

alter table csvsource add constraint fk_csvsource_csvconnection_id foreign key (csvconnection_id) references csvconnection (id) on delete restrict on update restrict;
create index ix_csvsource_csvconnection_id on csvsource (csvconnection_id);

alter table csvsource_link add constraint fk_csvsource_link_csvconnection_id foreign key (csvconnection_id) references csvconnection (id) on delete restrict on update restrict;
create index ix_csvsource_link_csvconnection_id on csvsource_link (csvconnection_id);

alter table csvsource_link_csvsource add constraint fk_csvsource_link_csvsource_csvsource_link foreign key (csvsource_link_id) references csvsource_link (id) on delete restrict on update restrict;
create index ix_csvsource_link_csvsource_csvsource_link on csvsource_link_csvsource (csvsource_link_id);

alter table csvsource_link_csvsource add constraint fk_csvsource_link_csvsource_csvsource foreign key (csvsource_id) references csvsource (id) on delete restrict on update restrict;
create index ix_csvsource_link_csvsource_csvsource on csvsource_link_csvsource (csvsource_id);

alter table column_metadata add constraint fk_column_metadata_tablemetadata_id foreign key (tablemetadata_id) references table_metadata (id) on delete restrict on update restrict;
create index ix_column_metadata_tablemetadata_id on column_metadata (tablemetadata_id);

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

alter table pivot_table add constraint fk_pivot_table_user_id foreign key (user_id) references user_list (id) on delete restrict on update restrict;
create index ix_pivot_table_user_id on pivot_table (user_id);

alter table pivot_table add constraint fk_pivot_table_sqlsource_id foreign key (sqlsource_id) references sqlsource (id) on delete restrict on update restrict;
create index ix_pivot_table_sqlsource_id on pivot_table (sqlsource_id);

alter table pivot_table add constraint fk_pivot_table_csvsource_id foreign key (csvsource_id) references csvsource (id) on delete restrict on update restrict;
create index ix_pivot_table_csvsource_id on pivot_table (csvsource_id);

alter table pivot_value add constraint fk_pivot_value_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_pivot_value_field_id on pivot_value (field_id);

alter table pivot_value add constraint fk_pivot_value_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_pivot_value_pivot_table_id on pivot_value (pivot_table_id);

alter table pivot_value add constraint fk_pivot_value_pivot_value_type_id foreign key (pivot_value_type_id) references pivot_value_type (id) on delete restrict on update restrict;
create index ix_pivot_value_pivot_value_type_id on pivot_value (pivot_value_type_id);

alter table sqldimension add constraint fk_sqldimension_sqlsource_id foreign key (sqlsource_id) references sqlsource (id) on delete restrict on update restrict;
create index ix_sqldimension_sqlsource_id on sqldimension (sqlsource_id);

alter table sqlsource add constraint fk_sqlsource_sqlconnection_id foreign key (sqlconnection_id) references sqlconnection (id) on delete restrict on update restrict;
create index ix_sqlsource_sqlconnection_id on sqlsource (sqlconnection_id);

alter table share_permission add constraint fk_share_permission_user_id foreign key (user_id) references user_list (id) on delete restrict on update restrict;
create index ix_share_permission_user_id on share_permission (user_id);

alter table share_permission add constraint fk_share_permission_pivot_table_id foreign key (pivot_table_id) references pivot_table (id) on delete restrict on update restrict;
create index ix_share_permission_pivot_table_id on share_permission (pivot_table_id);

alter table table_metadata add constraint fk_table_metadata_sqlconnection_id foreign key (sqlconnection_id) references sqlconnection (id) on delete restrict on update restrict;
create index ix_table_metadata_sqlconnection_id on table_metadata (sqlconnection_id);


# --- !Downs

alter table if exists csvsource drop constraint if exists fk_csvsource_csvconnection_id;
drop index if exists ix_csvsource_csvconnection_id;

alter table if exists csvsource_link drop constraint if exists fk_csvsource_link_csvconnection_id;
drop index if exists ix_csvsource_link_csvconnection_id;

alter table if exists csvsource_link_csvsource drop constraint if exists fk_csvsource_link_csvsource_csvsource_link;
drop index if exists ix_csvsource_link_csvsource_csvsource_link;

alter table if exists csvsource_link_csvsource drop constraint if exists fk_csvsource_link_csvsource_csvsource;
drop index if exists ix_csvsource_link_csvsource_csvsource;

alter table if exists column_metadata drop constraint if exists fk_column_metadata_tablemetadata_id;
drop index if exists ix_column_metadata_tablemetadata_id;

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

alter table if exists pivot_table drop constraint if exists fk_pivot_table_user_id;
drop index if exists ix_pivot_table_user_id;

alter table if exists pivot_table drop constraint if exists fk_pivot_table_sqlsource_id;
drop index if exists ix_pivot_table_sqlsource_id;

alter table if exists pivot_table drop constraint if exists fk_pivot_table_csvsource_id;
drop index if exists ix_pivot_table_csvsource_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_field_id;
drop index if exists ix_pivot_value_field_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_pivot_table_id;
drop index if exists ix_pivot_value_pivot_table_id;

alter table if exists pivot_value drop constraint if exists fk_pivot_value_pivot_value_type_id;
drop index if exists ix_pivot_value_pivot_value_type_id;

alter table if exists sqldimension drop constraint if exists fk_sqldimension_sqlsource_id;
drop index if exists ix_sqldimension_sqlsource_id;

alter table if exists sqlsource drop constraint if exists fk_sqlsource_sqlconnection_id;
drop index if exists ix_sqlsource_sqlconnection_id;

alter table if exists share_permission drop constraint if exists fk_share_permission_user_id;
drop index if exists ix_share_permission_user_id;

alter table if exists share_permission drop constraint if exists fk_share_permission_pivot_table_id;
drop index if exists ix_share_permission_pivot_table_id;

alter table if exists table_metadata drop constraint if exists fk_table_metadata_sqlconnection_id;
drop index if exists ix_table_metadata_sqlconnection_id;

drop table if exists csvconnection cascade;

drop table if exists csvsource cascade;

drop table if exists csvsource_link cascade;

drop table if exists csvsource_link_csvsource cascade;

drop table if exists column_metadata cascade;

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

drop table if exists sqldimension cascade;

drop table if exists sqlsource cascade;

drop table if exists share_permission cascade;

drop table if exists table_metadata cascade;

drop table if exists token cascade;

drop table if exists user_list cascade;

