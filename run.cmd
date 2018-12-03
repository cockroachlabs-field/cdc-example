docker-compose up --no-start
docker-compose start roach-source-0
docker-compose start roach-source-1
docker-compose start roach-source-2
docker-compose exec roach-source-0 /cockroach/cockroach init --insecure
docker-compose start lb
docker-compose start generator

docker-compose start roach-destination

TIMEOUT 10

docker-compose exec roach-destination /cockroach/cockroach sql --insecure --execute="CREATE DATABASE ycsb_backup;"
docker-compose exec roach-destination /cockroach/cockroach sql --insecure --database ycsb_backup --execute="CREATE TABLE usertable (ycsb_key INT8 NOT NULL, field1 STRING NULL, field2 STRING NULL, field3 STRING NULL, field4 STRING NULL, field5 STRING NULL, field6 STRING NULL, field7 STRING NULL, field8 STRING NULL, field9 STRING NULL, field10 STRING NULL)"


docker-compose start zookeeper
docker-compose start kafka

TIMEOUT 10

docker-compose exec kafka /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic cdc

TIMEOUT 10

docker-compose exec roach-source-0 /cockroach/cockroach sql --insecure --database ycsb --execute="CREATE CHANGEFEED FOR TABLE usertable INTO 'kafka://localhost:9092';"




