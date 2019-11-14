# Cockroach DB CDC Example

Based on https://www.cockroachlabs.com/docs/stable/change-data-capture.html#create-a-changefeed-connected-to-kafka

To get started, simply run `./run.sh CRDB_ORG_NAME CRDB_LICENSE_KEY` where `CRDB_ORG_NAME` is your CRDB Enterprise License Org and `CRDB_LICENSE_KEY` is your CRDB Enterprise License Key.  For now, executing `docker-compose up` directly is not supported.  To stop all services and cleanup you can run `kill.sh`.  This does a full system prune, so be careful (`docker system prune -a -f --volumes`)

The following services are started:
* `crdb-0` - Cockroach cluster node serving as CDC source
* `crdb-1` - Cockroach cluster node serving as CDC source
* `crdb-2` - Cockroach cluster node serving as CDC source
* `lb` - HAProxy in front of source nodes
* `roach-destination` - single Cockroach node serving as CDC destination
* `zookeeper` - required component of Confluent Kafka instance
* `kafka` - Confluent Kafka instance that stores CDC data


Once running the following UIs are available:

* Source Cockroach UI - http://localhost:8080
* HAProxy UI - http://localhost:8081
* Backup Cockroach UI - http://localhost:8082

## Helpful Commands

Use this to show running jobs in `source` cluster
```bash
docker-compose exec crdb-0 /cockroach/cockroach sql --insecure --execute="SHOW JOBS;"
```

Use this to see data in Kafka topic
```bash
docker-compose exec kafka /usr/bin/kafka-console-consumer --bootstrap-server=localhost:9092 --property print.key=true --from-beginning --topic=kv
```

Use this to verify data has been loaded into `destination`
```bash
docker-compose exec roach-destination /cockroach/cockroach sql --insecure --database ycsb_backup --execute="select count(*) from usertable;"
```

List all Kafka topics
```bash
docker-compose exec kafka /usr/bin/kafka-topics --list --zookeeper zookeeper:2181
```