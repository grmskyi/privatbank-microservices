# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/sender_service.jar
EXPOSE 8093
ENTRYPOINT ["java", "-jar", "/app/sender_service.jar"]