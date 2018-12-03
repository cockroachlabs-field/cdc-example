#CDC Example

Based on https://www.cockroachlabs.com/docs/stable/change-data-capture.html#create-a-changefeed-connected-to-kafka

To get started, simply run `run.sh`.  For now, executing `docker-compose up` directly is not supported.  To stop all services and cleanup you can run `kill.sh`.  This does a full system prune, so be careful (`docker system prune -a -f --volumes`)

The following services are started:
* `roach-0` - Cockroach cluster node serving as CDC source
* `roach-1` - Cockroach cluster node serving as CDC source
* `roach-2` - Cockroach cluster node serving as CDC source
* `lb` - Nginx LB in front of source nodes
* `generator` - runs `ycsb`
* `roach-backup` - single Cockroach node serving as CDC destination
* `zookeeper` - required component of Confluent Kafka instance
* `kafka` - Confluent Kafka instance that stores CDC data


Once running the following UIs are available:

* Source Cockroach UI - http://localhost:8080
* Backup Cockroach UI - http://localhost:8081

