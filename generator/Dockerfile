FROM openjdk:8-jdk

LABEL maintainer="tjveil@gmail.com"

ENV HOST=lb
ENV PORT=5432

RUN wget -qO- https://github.com/cockroachdb/docs/raw/master/v2.1/training/resources/crdb-ycsb-linux.tar.gz | tar xvz

CMD ./ycsb -duration 20m -tolerate-errors -concurrency 1 -max-rate 1 'postgresql://root@lb:5432?sslmode=disable'