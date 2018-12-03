#CDC Example

Based on https://www.cockroachlabs.com/docs/stable/change-data-capture.html#create-a-changefeed-connected-to-kafka

To get started, simply run `run.sh`.  For now executing `docker-compose up` directly is not supported.  To stop all services and cleanup you can run `kill.sh`.  This does a full system prune, so be careful (`docker system prune -a -f --volumes`)

Once running the following interfaces are available:

* Source Cockroach UI - http://localhost:8080
* Backup Cockroach UI - http://localhost:8081