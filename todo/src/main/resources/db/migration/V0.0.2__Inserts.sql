INSERT INTO teams (id, name, original_id) VALUES('b06a8598-d72f-11eb-b8bc-0242ac130003', 'customer', 1);
INSERT INTO teams (id, name, original_id) VALUES('1b8f9f84-d730-11eb-b8bc-0242ac130003', 'marketing', 2);

INSERT INTO users (id, team_id, username, email) VALUES('f1962369-e9cc-46a8-bc04-97bdc525f835', 'b06a8598-d72f-11eb-b8bc-0242ac130003', 'adm-customer', 'adm-customer@todo.com');
INSERT INTO users (id, team_id, username, email) VALUES('77efae11-e065-4fd4-b08c-b3a645e57d4e', '1b8f9f84-d730-11eb-b8bc-0242ac130003', 'adm-mkt', 'adm-mkt@todo.com');
INSERT INTO users (id, team_id, username, email) VALUES('c906a781-530a-4f5d-9ba7-80438117f72f', 'b06a8598-d72f-11eb-b8bc-0242ac130003', 'marco', 'marco@todo.com');
INSERT INTO users (id, team_id, username, email) VALUES('c8ab582c-1169-46d3-a641-b651db5a75c8', 'b06a8598-d72f-11eb-b8bc-0242ac130003', 'peter', 'peter@todo.com');
INSERT INTO users (id, team_id, username, email) VALUES('71aa18eb-b4e8-45c6-a31d-4bb831d13788', '1b8f9f84-d730-11eb-b8bc-0242ac130003', 'maria', 'maria@todo.com');

INSERT INTO todos (id, team_id, original_id, description) VALUES('9b51cce0-d732-11eb-b8bc-0242ac130003', 'b06a8598-d72f-11eb-b8bc-0242ac130003', 1, 'shared TO DO LIST of customer');
INSERT INTO todos (id, team_id, original_id, description) VALUES('f0f045f0-d732-11eb-b8bc-0242ac130003', '1b8f9f84-d730-11eb-b8bc-0242ac130003', 2, 'shared TO DO LIST of marketing');
INSERT INTO todos (id, user_id, original_id, description) VALUES('864f388a-d734-11eb-b8bc-0242ac130003', 'c906a781-530a-4f5d-9ba7-80438117f72f', 3, 'TO DO LIST of Marco');
INSERT INTO todos (id, user_id, original_id, description) VALUES('8901f9d4-d732-11eb-b8bc-0242ac130003', 'c8ab582c-1169-46d3-a641-b651db5a75c8', 4, 'TO DO LIST of Peter');
INSERT INTO todos (id, user_id, original_id, description) VALUES('056492c0-d733-11eb-b8bc-0242ac130003', '71aa18eb-b4e8-45c6-a31d-4bb831d13788', 5, 'TO DO LIST of Maria');

INSERT INTO notes (id, user_id, todo_id, description) VALUES('10636f62-d732-11eb-b8bc-0242ac130003', 'c8ab582c-1169-46d3-a641-b651db5a75c8', '8901f9d4-d732-11eb-b8bc-0242ac130003', 'note of Peter');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('3f3f0ac6-d732-11eb-b8bc-0242ac130003', 'c8ab582c-1169-46d3-a641-b651db5a75c8', '8901f9d4-d732-11eb-b8bc-0242ac130003', 'note changed of Peter');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('53467b94-d732-11eb-b8bc-0242ac130003', 'c906a781-530a-4f5d-9ba7-80438117f72f', '864f388a-d734-11eb-b8bc-0242ac130003', 'note of Marco');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('d72970ca-d916-11eb-b8bc-0242ac130003', 'c906a781-530a-4f5d-9ba7-80438117f72f', '864f388a-d734-11eb-b8bc-0242ac130003', 'note changed of Marco');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('58b5c2ce-d732-11eb-b8bc-0242ac130003', 'c8ab582c-1169-46d3-a641-b651db5a75c8', '9b51cce0-d732-11eb-b8bc-0242ac130003', 'note shared of Peter');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('5d52bfee-d732-11eb-b8bc-0242ac130003', 'c906a781-530a-4f5d-9ba7-80438117f72f', '9b51cce0-d732-11eb-b8bc-0242ac130003', 'note shared of Marco');
INSERT INTO notes (id, user_id, todo_id, description) VALUES('635346c0-d732-11eb-b8bc-0242ac130003', '71aa18eb-b4e8-45c6-a31d-4bb831d13788', 'f0f045f0-d732-11eb-b8bc-0242ac130003', 'note shared of Maria');

INSERT INTO versions (id, note_parent_id, new_note_id) VALUES('09d15042-d732-11eb-b8bc-0242ac130003', '10636f62-d732-11eb-b8bc-0242ac130003', '3f3f0ac6-d732-11eb-b8bc-0242ac130003');
INSERT INTO versions (id, note_parent_id, new_note_id) VALUES('4b569874-d917-11eb-b8bc-0242ac130003', '53467b94-d732-11eb-b8bc-0242ac130003', 'd72970ca-d916-11eb-b8bc-0242ac130003');
