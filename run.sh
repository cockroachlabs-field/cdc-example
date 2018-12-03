#!/bin/bash


## start original database and load balancer
docker-compose up --no-start
docker-compose start roach-0
docker-compose start roach-1
docker-compose start roach-2
docker-compose exec roach-0 /cockroach/cockroach init --insecure
docker-compose start lb

echo "sleeping..."
sleep 5

# configure license
docker-compose exec roach-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING cluster.organization = 'tv';"
docker-compose exec roach-0 /cockroach/cockroach sql --insecure --execute="SET CLUSTER SETTING enterprise.license = 'crl-0-EPGo8OMFGAIiAnR2';"

# start generator app on original database
docker-compose start generator

# start backup database nodes
docker-compose start roach-backup

echo "sleeping..."
sleep 10

# create backup database
docker-compose exec roach-backup /cockroach/cockroach sql --insecure --execute="CREATE DATABASE ycsb_backup;"
docker-compose exec roach-backup /cockroach/cockroach sql --insecure --database ycsb_backup --execute="CREATE TABLE usertable (ycsb_key INT8 NOT NULL, field1 STRING NULL, field2 STRING NULL, field3 STRING NULL, field4 STRING NULL, field5 STRING NULL, field6 STRING NULL, field7 STRING NULL, field8 STRING NULL, field9 STRING NULL, field10 STRING NULL)"

# start kafka and zk
docker-compose start zookeeper
docker-compose start kafka

echo "sleeping..."
sleep 10

# create kafka topic
docker-compose exec kafka /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic usertable

echo "sleeping..."
sleep 10

# start changefeed
docker-compose exec roach-0 /cockroach/cockroach sql --insecure --database ycsb --execute="CREATE CHANGEFEED FOR TABLE usertable INTO 'kafka://kafka:9092';"




