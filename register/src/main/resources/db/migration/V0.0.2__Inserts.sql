INSERT INTO teams (id, name) VALUES(1, 'customer');
INSERT INTO teams (id, name) VALUES(2, 'marketing');

INSERT INTO users (id, original_id, team_id, username, email) VALUES(1, 'f1962369-e9cc-46a8-bc04-97bdc525f835', 1, 'adm-customer', 'adm-customer@todo.com');
INSERT INTO users (id, original_id, team_id, username, email) VALUES(2, '77efae11-e065-4fd4-b08c-b3a645e57d4e', 2, 'adm-mkt', 'adm-mkt@todo.com');
INSERT INTO users (id, original_id, team_id, username, email) VALUES(3, 'c906a781-530a-4f5d-9ba7-80438117f72f', 1, 'marco', 'marco@todo.com');
INSERT INTO users (id, original_id, team_id, username, email) VALUES(4, 'c8ab582c-1169-46d3-a641-b651db5a75c8', 1, 'peter', 'peter@todo.com');
INSERT INTO users (id, original_id, team_id, username, email) VALUES(5, '71aa18eb-b4e8-45c6-a31d-4bb831d13788', 2, 'maria', 'maria@todo.com');

INSERT INTO todos (id, team_id, description) VALUES(1, 1, 'shared TO DO LIST of customer');
INSERT INTO todos (id, team_id, description) VALUES(2, 2, 'shared TO DO LIST of marketing');
INSERT INTO todos (id, user_id, description) VALUES(3, 3, 'TO DO LIST of Marco');
INSERT INTO todos (id, user_id, description) VALUES(4, 4, 'TO DO LIST of Peter');
INSERT INTO todos (id, user_id, description) VALUES(5, 5, 'TO DO LIST of Maria');

