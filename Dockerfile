FROM eclipse-temurin:17-jdk
MAINTAINER Traversium Developers
WORKDIR /opt/report-service

COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/opt/report-service/app.jar"]