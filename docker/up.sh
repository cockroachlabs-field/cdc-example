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

## start source database
docker-compose up --no-start
docker-compose start roach-source

echo "waiting for source to initialize..."
sleep 5

# configure license
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING cluster.organization = '${CRDB_ORG_NAME}';"
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING enterprise.license = '${CRDB_LICENSE_KEY}';"
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING kv.rangefeed.enabled = true;"
docker-compose exec roach-source /cockroach/cockroach sql --insecure --execute="CREATE DATABASE source;"

# start destination database
docker-compose start roach-destination

echo "waiting for destination to initialize..."
sleep 5

docker-compose exec roach-destination /cockroach/cockroach sql --insecure --execute="CREATE DATABASE destination;"

# start kafka and zk
docker-compose start zookeeper
docker-compose start kafka