# Dockerfile for the generating the webapp image
#
# To build:
#
# docker build -t docker.lib.umd.edu/fcrepo-webapp:<VERSION> -f Dockerfile .
#
# where <VERSION> is the Docker image version to create.
FROM maven:3.6.3-jdk-8-slim AS compile

ENV SOURCE_DIR /opt/umd-fcrepo-webapp
COPY src $SOURCE_DIR/src
COPY pom.xml $SOURCE_DIR
WORKDIR $SOURCE_DIR
RUN mvn package -DwarFileName=umd-fcrepo-webapp

# Note: Specifying SHA256 hash to ensure Docker base image consistency
FROM tomcat:7.0.109-jdk8-openjdk-slim-buster@sha256:50a7b08272c0ac084bd03f39b5967321da43d96539043608384194bc54f744de

# default context path is the root, making the full URL e.g. http://localhost:8080/
ENV CONTEXT_PATH=""
# default heap size is 2 GB
ENV TOMCAT_HEAP=2048m

RUN mkdir -p /opt/umd-fcrepo-webapp
COPY --from=compile /opt/umd-fcrepo-webapp/target/umd-fcrepo-webapp.war /opt/umd-fcrepo-webapp/
COPY setenv.sh /usr/local/tomcat/bin/
COPY server.xml /usr/local/tomcat/conf/

VOLUME /var/umd-fcrepo-webapp
# for the store-and-forward broker
VOLUME /var/activemq
