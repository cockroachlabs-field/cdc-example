#!/bin/bash

CRDB_ORG_NAME=$1
CRDB_LICENSE_KEY=$2

if [[ -z "$CRDB_ORG_NAME" ]]; then
    echo "Must provide CRDB_ORG_NAME as the first parameter. Example './run.sh CRDB_ORG_NAME CRDB_LICENSE_KEY'." 1>&2
    exit 1
fi

if [[ -z "$CRDB_LICENSE_KEY" ]]; then
    echo "Must provide CRDB_LICENSE_KEY as the second parameter. Example './run.sh CRDB_ORG_NAME CRDB_LICENSE_KEY'." 1>&2
    exit 1
fi

## start source database and load balancer
docker-compose up --no-start
docker-compose start crdb-0
docker-compose start crdb-1
docker-compose start crdb-2
docker-compose start lb

echo "waiting for source to initialize..."
sleep 5

# configure license
docker-compose exec crdb-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING cluster.organization = '${CRDB_ORG_NAME}';"
docker-compose exec crdb-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING enterprise.license = '${CRDB_LICENSE_KEY}';"
docker-compose exec crdb-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING kv.rangefeed.enabled = true;"

# start kv workload on the source database
docker-compose exec crdb-0 /cockroach/cockroach workload init kv 'postgresql://root@localhost:26257?sslmode=disable'

# start destination database node
docker-compose start roach-destination

echo "waiting for destination to start..."
sleep 10

# create backup database
docker-compose exec roach-destination /cockroach/cockroach workload init kv 'postgresql://root@localhost:26257?sslmode=disable'

# start kafka and zk
docker-compose start zookeeper
docker-compose start kafka

echo "waiting for kafka and zookeeper to start..."
sleep 20

# create kafka topic
docker-compose exec kafka /usr/bin/kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic kv

echo "waiting for topic to be created before creating changefeed..."
sleep 10

# start changefeed
docker-compose exec crdb-0 /cockroach/cockroach sql --insecure --database kv --execute="CREATE CHANGEFEED FOR TABLE kv INTO 'kafka://kafka:9092';"

echo "waiting for changefeed to be applied before generating load on source database"
sleep 10
docker-compose exec crdb-0 /cockroach/cockroach workload run kv --duration=10m 'postgresql://root@localhost:26257?sslmode=disable'