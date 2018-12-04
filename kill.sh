#!/bin/bash

docker-compose exec kafka /usr/bin/kafka-topics --delete --zookeeper zookeeper:2181 --topic usertable

docker-compose down

docker system prune -a -f --volumes




