INSERT INTO teams (name) VALUES('customer');
INSERT INTO teams (name) VALUES('marketing');

INSERT INTO users (original_id, team_id, username, email) VALUES('f1962369-e9cc-46a8-bc04-97bdc525f835', 1, 'adm-customer', 'adm-customer@todo.com');
INSERT INTO users (original_id, team_id, username, email) VALUES('77efae11-e065-4fd4-b08c-b3a645e57d4e', 2, 'adm-mkt', 'adm-mkt@todo.com');
INSERT INTO users (original_id, team_id, username, email) VALUES('c906a781-530a-4f5d-9ba7-80438117f72f', 1, 'marco', 'marco@todo.com');
INSERT INTO users (original_id, team_id, username, email) VALUES('c8ab582c-1169-46d3-a641-b651db5a75c8', 1, 'peter', 'peter@todo.com');
INSERT INTO users (original_id, team_id, username, email) VALUES('71aa18eb-b4e8-45c6-a31d-4bb831d13788', 2, 'maria', 'maria@todo.com');

INSERT INTO todos (team_id, description) VALUES(1, 'shared TO DO LIST of customer');
INSERT INTO todos (team_id, description) VALUES(2, 'shared TO DO LIST of marketing');