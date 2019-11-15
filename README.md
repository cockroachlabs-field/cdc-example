# Cockroach DB CDC Example

Based on https://www.cockroachlabs.com/docs/stable/change-data-capture.html#create-a-changefeed-connected-to-kafka

This example consists of 2 parts:
* A collection of Docker Compose services
    * `roach-source` - single CockroachDB node serving as CDC source
    * `roach-destination` - single CockroachDB node serving as CDC destination
    * `zookeeper` - required component of Confluent Kafka instance
    * `kafka` - Confluent Kafka instance that stores CDC data
* Two Spring Boot applications... one for inserting data into the `source` CockroachDB instance (the producer) and one for reading from Kafka and writing to the `destination` CockroachDB instance (the consumer).

## How to Run

1) Execute `./up.sh CRDB_ORG_NAME CRDB_LICENSE_KEY` where `CRDB_ORG_NAME` is your CRDB Enterprise License Org and `CRDB_LICENSE_KEY` is your CRDB Enterprise License Key.  For now, executing `docker-compose up` directly is not supported.  This command will start the required Docker containers.  
2) You can verify that each CockroachDB instance is running by visting the following URLS:
    * Source Cockroach UI - http://localhost:8080
    * Destination Cockroach UI - http://localhost:8081
1) Once the services have started properly, start the `producer` Spring Boot application.  This will slowly load data into the `source` database.
    ```
    java -jar producer-0.0.1-SNAPSHOT.jar
    ```
1) While the `producer` is running, start the `consumer` Spring Boot application.  The `consumer` will read from Kafka and populate the `destination` database.
    ```
    java -jar consumer-0.0.1-SNAPSHOT.jar
    ```
1) When you are done, you can stop all services with `down.sh`.  To do a full system prune run `prune.sh`.

## Helpful Commands

Use this to show running jobs in `roach-source` cluster
```bash
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="SHOW JOBS;"
```

Use this to see data in Kafka topic
```bash
docker-compose exec kafka /usr/bin/kafka-console-consumer --bootstrap-server=localhost:9092 --from-beginning --topic=source_table
```

Use this to view data in the `soucre` database
```bash
docker-compose exec roach-source /cockroach/cockroach sql --insecure --database source --execute="select count(*) from source_table;"
```

Use this to verify data has been loaded into the `destination` database 
```bash
docker-compose exec roach-destination /cockroach/cockroach sql --insecure --database destination --execute="select count(*) from destination_table;"
```

List all Kafka topics
```bash
docker-compose exec kafka /usr/bin/kafka-topics --list --zookeeper zookeeper:2181
```