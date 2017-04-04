INSERT INTO user_list VALUES (nextval('user_list_id_seq'::regclass), 'test.user@gmail.com',
    'Test User', 'test', NULL);

INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'count', 'Count');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'sum', 'Sum');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'mean', 'Mean');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'max', 'Max');
INSERT INTO pivot_value_type VALUES(nextval('pivot_value_type_id_seq'::regclass), 'min', 'Min');
INSERT INTO pivot_value_type VALUES(nextvalactiv('pivot_value_type_id_seq'::regclass), 'std_dev', 'StdDev');