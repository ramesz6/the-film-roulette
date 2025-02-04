# Stage 1
# initialize build and set base image for first stage
FROM maven:3-amazoncorretto-23 AS builder
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /app
# copy just pom.xml
COPY pom.xml .
# go-offline using the pom.xml
# setup the download all the dependencies
RUN mvn dependency:go-offline
# copy your other files
COPY ./src ./src
# compile the source code and package it in a jar file
RUN mvn clean install -Dmaven.test.skip=true


# Stage 2 run the generated jar file
# set base image for second stage
FROM amazoncorretto:23-alpine
# set deployment directory
WORKDIR /app
# copy over the built artifact from the maven image
COPY --from=builder /app/target/*-0.0.1-SNAPSHOT.jar /app/backend.jar
CMD java -jar /app/backend.jar