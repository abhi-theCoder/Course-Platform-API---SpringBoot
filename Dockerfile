# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY seed_data ./seed_data

# Production environment variables defaults
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=validate
ENV SPRING_JPA_SHOW_SQL=false
ENV SWAGGER_ENABLED=false

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
