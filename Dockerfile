FROM openjdk:17-jdk-slim
COPY target/java-generic-project-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
