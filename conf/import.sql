/**
Small script to initialize the database content required before using the application for the first time
It is possible to change the email address and user name to a value of your liking
 */

INSERT INTO user_list VALUES (nextval('user_list_id_seq'::regclass), 'test.user@gmail.com',
    'Test User', 'test', NULL);

INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'count', 'Count');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'sum', 'Sum');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'mean', 'Mean');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'max', 'Max');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'min', 'Min');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'std_dev', 'StdDev');