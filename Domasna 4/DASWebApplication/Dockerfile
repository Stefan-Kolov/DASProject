FROM openjdk:23-jdk-slim
WORKDIR /app
COPY target/DASWebApplication-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 9090
