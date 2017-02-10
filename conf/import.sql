
/**** PIVOT TABLE ******/
INSERT INTO pivot_table VALUES(nextval('pivot_table_id_seq'::regclass));

/****  FIELDS *******/

INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Date');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Client Name');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Company Name');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Company Nb Employees');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Product Name');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Quantity');
INSERT INTO field VALUES(nextval('field_id_seq'::regclass), 'Product Unit Price');

UPDATE field SET pivot_table_id = 1;

INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'count', 'Count');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'sum', 'Sum');