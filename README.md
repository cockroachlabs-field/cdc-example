# Cockroach DB CDC Example

Based on https://www.cockroachlabs.com/docs/stable/change-data-capture.html#create-a-changefeed-connected-to-kafka

To get started, simply run `./up.sh CRDB_ORG_NAME CRDB_LICENSE_KEY` where `CRDB_ORG_NAME` is your CRDB Enterprise License Org and `CRDB_LICENSE_KEY` is your CRDB Enterprise License Key.  For now, executing `docker-compose up` directly is not supported.  To stop all services run `down.sh`.  To do a full system prune run `prune.sh`.

The following services are started:
* `roach-source` - single CockroachDB node serving as CDC source
* `roach-destination` - single CockroachDB node serving as CDC destination
* `zookeeper` - required component of Confluent Kafka instance
* `kafka` - Confluent Kafka instance that stores CDC data


Once running the following UIs are available:

* Source Cockroach UI - http://localhost:8080
* Destination Cockroach UI - http://localhost:8081

## Helpful Commands

Use this to show running jobs in `roach-source` cluster
```bash
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="SHOW JOBS;"
```

Use this to see data in Kafka topic
```bash
docker-compose exec kafka /usr/bin/kafka-console-consumer --bootstrap-server=localhost:9092 --from-beginning --topic=source_table
```

Use this to verify data has been loaded into `roach-destination`
```bash
docker-compose exec roach-destination /cockroach/cockroach sql --insecure --database destination --execute="select count(*) from destination_table;"
```

List all Kafka topics
```bash
docker-compose exec kafka /usr/bin/kafka-topics --list --zookeeper zookeeper:2181
```