
#!/bin/sh

docker-compose down && docker-compose up -d

cd todo
mvn clean install && mvn quarkus:dev &

cd ../debezium
sh ./config.sh

# cd ../register
# sh mvn clean install && mvn quarkus:dev &