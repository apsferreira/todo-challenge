-- public.teams definition

-- Drop table

-- DROP TABLE public.teams;

CREATE TABLE public.teams (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	"name" varchar(255) NULL,
	CONSTRAINT teams_pkey PRIMARY KEY (id)
);

-- public.todos definition

-- Drop table

-- DROP TABLE public.todos;

CREATE TABLE public.todos (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	description varchar(255) NULL,
	team_id int8 NULL,
	user_id int8 NULL,
	CONSTRAINT todos_pkey PRIMARY KEY (id)
);


-- public.todos foreign keys

-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	email varchar(255) NULL,
	original_id uuid NULL,
	username varchar(255) NULL,
	team_id int8 NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);


-- public.users foreign keys