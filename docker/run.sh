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
docker-compose start roach-source-0
docker-compose start roach-source-1
docker-compose start roach-source-2
docker-compose start lb

echo "waiting for source to initialize..."
sleep 5

# configure license
docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING cluster.organization = '${CRDB_ORG_NAME}';"
docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING enterprise.license = '${CRDB_LICENSE_KEY}';"
docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING kv.rangefeed.enabled = true;"
docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --execute="CREATE DATABASE source_db;"

# start destination database node
docker-compose start roach-destination

echo "waiting for destination to start..."
sleep 5

docker-compose exec roach-destination /cockroach/cockroach sql --insecure --execute="CREATE DATABASE destination_db;"


# create backup database
#docker-compose exec roach-destination /cockroach/cockroach sql --insecure --execute="CREATE DATABASE ycsb_backup;"
#ocker-compose exec roach-destination /cockroach/cockroach sql --insecure --database ycsb_backup --execute="CREATE TABLE usertable (ycsb_key STRING NOT NULL, field1 STRING NULL, field2 STRING NULL, field3 STRING NULL, field4 STRING NULL, field5 STRING NULL, field6 STRING NULL, field7 STRING NULL, field8 STRING NULL, field9 STRING NULL, field10 STRING NULL);"


# start kafka and zk
docker-compose start zookeeper
docker-compose start kafka

echo "waiting for kafka and zookeeper to start..."
sleep 20

# create kafka topic
docker-compose exec kafka /usr/bin/kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic usertable

#echo "waiting for topic to be created before creating changefeed..."
#sleep 10

# start changefeed
#ocker-compose exec roach-source-0 /cockroach/cockroach workload init ycsb

#docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --database ycsb --execute="CREATE CHANGEFEED FOR TABLE usertable INTO 'kafka://kafka:9092';"

#docker-compose exec roach-source-0 /cockroach/cockroach workload run ycsb --duration=30m

#docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --database bank --execute="select * from bank;"

