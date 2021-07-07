CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE teams (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	name varchar(200) NOT NULL,
	original_id int8 DEFAULT NULL,
	CONSTRAINT team_pkey PRIMARY KEY (id)
);

CREATE TABLE users (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	team_id uuid DEFAULT NULL,
	username varchar(200) NULL,
	email varchar(200) NULL,
	CONSTRAINT user_pkey PRIMARY KEY (id)
);

ALTER TABLE users ADD CONSTRAINT user_team FOREIGN KEY (team_id) REFERENCES teams(id);

CREATE TABLE todos (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	user_id uuid DEFAULT NULL, 
	team_id uuid DEFAULT NULL,
	original_id int8 DEFAULT NULL,
	description varchar(200) NULL,
	CONSTRAINT todo_pkey PRIMARY KEY (id)
);

ALTER TABLE todos ADD CONSTRAINT todo_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE todos ADD CONSTRAINT todo_team FOREIGN KEY (team_id) REFERENCES teams(id);

CREATE TABLE notes (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	user_id uuid NOT NULL,
	todo_id uuid NOT NULL,
	description varchar(500),
	done BOOLEAN default FALSE,
	favorite BOOLEAN default FALSE,	
	subject_jwt varchar(20) DEFAULT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT note_pkey PRIMARY KEY (id)
);

ALTER TABLE notes ADD CONSTRAINT note_todo FOREIGN KEY (todo_id) REFERENCES todos(id);
ALTER TABLE notes ADD CONSTRAINT note_user FOREIGN KEY (user_id) REFERENCES users(id);

CREATE TABLE versions (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	note_parent_id uuid NOT NULL,
	new_note_id uuid NOT NULL,
    version TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT version_pkey PRIMARY KEY (id)
);

ALTER TABLE versions ADD CONSTRAINT version_note_parent FOREIGN KEY (note_parent_id) REFERENCES notes(id);
ALTER TABLE versions ADD CONSTRAINT version_new_note FOREIGN KEY (new_note_id) REFERENCES notes(id);