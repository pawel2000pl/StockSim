# syntax=docker/dockerfile:1

FROM maven:3.9.3-eclipse-temurin-17-focal

RUN mkdir -p /home/app
COPY src /home/app/src
COPY pom.xml /home/app/
COPY mvnw /home/app/
WORKDIR /home/app/
RUN mvn -f /home/app/pom.xml clean package
RUN cp /home/app/target/*.war /home/app/zti.war

FROM tomee:9.0.0-jre17-ubuntu-plume
ENV CATALINA_BASE="/usr/local/tomee"
ENV FILE_NAME="StockSim-1.0-SNAPSHOT.war"
RUN apt update
RUN apt install python3 python3-pip -y
RUN python3 -m pip install cherrypy requests
RUN mkdir -p "/frontend"

COPY --from=0 "/home/app/zti.war" "$CATALINA_BASE/webapps/$FILE_NAME"
COPY "frontend" "/frontend"
COPY "starter.sh" "/starter.sh"

RUN sed -i -e 's/8080/8081/' "$CATALINA_BASE/conf/server.xml"

EXPOSE 8080
EXPOSE 8081

CMD ["bash", "/starter.sh"]
