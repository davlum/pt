/****** USERS **********/
INSERT INTO user_list VALUES (nextval('user_list_id_seq'::regclass), 'soufiane.imanssar@gmail.com',
    'Soufiane Imanssar', 'asdfasdfasdf', NULL, NULL, '2017-01-01 00:00:00.000');


/**** PIVOT TABLE ******/
INSERT INTO pivot_table VALUES(nextval('pivot_table_id_seq'::regclass));

/****  FIELDS *******/

/*INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Date', 'Date');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Client Name', 'String');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Company Name', 'String');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Company Nb Employees', 'Integer');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Product Name', 'String');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Quantity', 'Integer');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Product Unit Price', 'Double');*/

INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'end_date', 'Date');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'end_station', 'Integer');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'duration_ms', 'Integer');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'end_station_number', 'String');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'account_type', 'String');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'start_station', 'Integer');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'start_date', 'Date');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'start_station_number', 'String');

UPDATE field SET pivot_table_id = 1;

INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'count', 'Count');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'sum', 'Sum');