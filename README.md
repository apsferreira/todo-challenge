# TO DO List - Ojingo Code Challenge

Code challenge related to creating an API that supports requests for to do list app.


## Stack

This project was built using the technologies below:

- [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Postgresql](https://www.postgresql.org/)
- [Vert.x](https://www.postgresql.org/)
- [Graylog](https://www.graylog.org/)
- [Microprofile](https://microprofile.io/)
- [Docker](https://www.docker.com/)
- [Flyway](https://flywaydb.org/)
- [Quarkus](http://quarkus.io/)
- [Debezium](https://debezium.io/)
- [Mapstruct](https://mapstruct.org/)
- [Testcontainers](https://www.testcontainers.org/)
- [Apache kafka](https://kafka.apache.org/)
- [Keycloak](https://www.keycloak.org/)
- [Maven](https://maven.apache.org/)

## Explain the project

This project contains two microservices: register and todo.

The register microservice is responsible for CRUD operations related of Teams, CRUD operations related of Shared To Do Lists and user association a team. 

The todo microservice is responsible for CRUD of Notes, set favorite notes  and set notes how done.

In this project, we have two administrative profiles. the profile administrator is responsible for create shared to do lists and associate a user in a team on register microservice and this profile has all permissions of the second administrative profile, the user, this profile is responsible for create notes and access the shared to do lists of the team the user is associated with.


![Use Cases](/documentation/use_case.png)
Format: ![Alt Text](url)


The keycloak is the solution responsible for register an user and set the permissions this. 

Graylog is the solution for centralized application logging.

jaeger is the solution for analyzing application requests.

deebzium is the solution for sending operations done on tables that are in the postgres database to kafka.

Graylog is the solution for centralized application logging.

jaeger is the solution for analyzing application requests.

debezium is the solution for sending operations done on tables that are in the postgres database to kafka. this operations are integrated in all microservices.

## Architecture

![Architecture](/documentation/architecture.png)
Format: ![Alt Text](url)


## DB Models of the Microservices

![Model DB MS Register](/documentation/model_db_register.png)
Format: ![Alt Text](url)

![Model DB MS TODO](/documentation/model_db_todo.png)
Format: ![Alt Text](url)

## Class Diagrams of the Microservices

![Class Diagram MS Register](/documentation/class_register.png)
Format: ![Alt Text](url)

![Class Diagram MS TODO](/documentation/class_todo.png)
Format: ![Alt Text](url)

[Document for a best visualization](/documentation/class_diagram.pdf)


## Endpoints

### Register MS

![Teams Endpoints](/documentation/teams_endpoints_register.png)
Format: ![Alt Text](url)

![Todos Endpoints](/documentation/todos_endpoints_register.png)
Format: ![Alt Text](url)

![Users Endpoints](/documentation/users_endpoints_register.png)
Format: ![Alt Text](url)


### TODO MS

![Notes Endpoints](/documentation/teams_endpoints_todo.png)
Format: ![Alt Text](url)

![Todos Endpoints](/documentation/todos_endpoints_todo.png)
Format: ![Alt Text](url)

![Users Endpoints](/documentation/users_endpoints_todo.png)
Format: ![Alt Text](url)


## Getting started

### Prerequisites

- docker
- docker-compose
- maven
- java 11

### Start Application

for execute this application run the command below to execute Microservices

```bash
$ chmod +x startup.sh
$ ./start.sh
```

## License

Copyright [Antonio Pedro Ferreira](https://github.com/apsferreira).

Released under an [MIT License](https://opensource.org/licenses/MIT).