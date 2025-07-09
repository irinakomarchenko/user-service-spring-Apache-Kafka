FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl
COPY target/discovery-server-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]