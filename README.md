# Overview
This microservice architecture consists of five microservices designed to handle client data through a sequence of operations. Each microservice has a specific role, from receiving HTTP requests to saving data into an SQL database. The interaction between these services is facilitated by RabbitMQ.

## Microservice Architecture

- [Sender Service](#Service1)
- [First Receiver Service](#Service2)
- [Second Receiver Service](#Service3)
- [Third Receiver Service](#Service4)
- [Data Storage Service](#Service5)
- [Confirmation that the project is working](#Confirmation)

![Alt text](screenshots_for_github/project_schema.PNG?raw=true "Project Schema")
## Sender Service

### Description
This service accepts an HTTP JSON request with a client ID and sends this ID to the RabbitMQ exchange.

### Classes
- `ClientDataSenderController`: Handles HTTP requests.
- `ClientDataServiceImpl`: Implements the service logic to send client ID to RabbitMQ.
- `RabbitMQSenderConfig`: Configures RabbitMQ components.

### DockerFile
```dockerfile
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
```
### Example Request
```http request
POST /api/v1/send
Content-Type: application/text
{
  "clientId": "12345"
}
```
## First Receiver Service
### Description
This service listens to messages from RabbitMQ, mocks a call to an external service to fetch client data, and forwards the data to the next service via RabbitMQ.
### Classes
- `FirstReceiverServiceImpl`: Implements the service logic to fetch client data.
- `RabbitMQFirstReceiverConfiguration`: Configures RabbitMQ components and listeners.
- `RabbitMQConfig`: Provides common RabbitMQ configurations such as message converters.
### DockerFile
```dockerfile
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/first_receiver_service.jar
EXPOSE 8094
ENTRYPOINT ["java", "-jar", "/app/first_receiver_service.jar"]
```
## Second  Receiver Service
### Description
This service listens to messages from RabbitMQ, mocks a call to another external service to fetch additional client data, and forwards the data to the next service via RabbitMQ.
### Classes
- `SecondReceiverServiceImpl`: Implements the service logic to fetch additional client data.
- `RabbitMQSecondReceiverConfiguration`: Configures RabbitMQ components and listeners.
- `RabbitMQConfig`: Provides common RabbitMQ configurations such as message converters.
### DockerFile
```dockerfile
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/second_receiver_service.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "/app/second_receiver_service.jar"]
```
## Third Receiver Service
### Description
This service listens to messages from RabbitMQ, mocks a call to a third external service to fetch more client data, and forwards the data to the final data storage service via RabbitMQ
### Classes
- `ThirdReceiverServiceImpl`: Implements the service logic to fetch more client data.
- `RabbitMQThirdReceiverConfiguration`: Configures RabbitMQ components and listeners.
- `RabbitMQConfig`: Provides common RabbitMQ configurations such as message converters.
### DockerFile
```dockerfile
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/third_receiver_service.jar
EXPOSE 8096
ENTRYPOINT ["java", "-jar", "/app/third_receiver_service.jar"]
```
## Data Storage Service
### Description
This service listens to messages from RabbitMQ and saves the received client data into an SQL database using JPA.
### Classes
- `DataStorageServiceImpl`: Implements the service logic to save client data.
- `RabbitMQDataStorageConfiguration`: Configures RabbitMQ components and listeners.
- `DataStorageRepository`: Repository interface for JPA operations.
- `RabbitMQConfig`: Provides common RabbitMQ configurations such as message converters.
- `ClientDataMapper`: provides mapping configurations between DTOs and entities using MapStruct. This mapper is used in the Data Storage Service to convert ClientDataDTO to ClientData and vice versa.
### DockerFile
```dockerfile
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/data_storage_service.jar
EXPOSE 8097
ENTRYPOINT ["java", "-jar", "/app/data_storage_service.jar"]
```
## Docker Compose
```yaml
version: '3.8'
services:
  rabbitmq:
    image: rabbitmq:3.10.7-management
    hostname: rabbitmq
    restart: unless-stopped
    environment:
      - RABBITMQ_DEFAULT_USER=user
      - RABBITMQ_DEFAULT_PASS=sa
      - RABBITMQ_DEFAULT_VHOST=vhost
      - RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS=-rabbit log_levels [{connection,error},{default,error}]
    volumes:
      - ./rabbitmq:/var/lib/rabbitmq
    ports:
      - 15672:15672
      - 5672:5672
    healthcheck:
      test: ["CMD-SHELL", "rabbitmqctl status"]
      interval: 30s
      timeout: 10s
      retries: 5

  postgres:
    image: postgres:13
    environment:
      - POSTGRES_DB=postgres
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - ./postgres:/var/lib/postgresql/data
    ports:
      - 5432:5432
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 30s
      timeout: 10s
      retries: 5

  sender_service:
    build:
      context: ./sender_service
      dockerfile: Dockerfile
    ports:
      - 8093:8093
    environment:
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      rabbitmq:
        condition: service_healthy

  first_receiver_service:
    build:
      context: ./first_receiver_service
      dockerfile: Dockerfile
    ports:
      - 8094:8094
    environment:
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      rabbitmq:
        condition: service_healthy

  second_receiver_service:
    build:
      context: ./second_receiver_service
      dockerfile: Dockerfile
    ports:
      - 8095:8095
    environment:
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      rabbitmq:
        condition: service_healthy

  third_receiver_service:
    build:
      context: ./third_receiver_service
      dockerfile: Dockerfile
    ports:
      - 8096:8096
    environment:
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      rabbitmq:
        condition: service_healthy

  data_storage_service:
    build:
      context: ./data_storage_service
      dockerfile: Dockerfile
    ports:
      - 8097:8097
    environment:
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      rabbitmq:
        condition: service_healthy
      postgres:
        condition: service_healthy
```
## Confirmation that the project is working
- Let's start with saving data by the endpoint service. As you can see, the Clients Data table successfully saves data by clientID:
![Alt text](screenshots_for_github/database_table.PNG?raw=true "Data from table")

- Working architecture and deployed project in Docker:
  ![Alt text](screenshots_for_github/docker_structure.PNG?raw=true "Docker structure")
- Logs from the raised project:
```terminal
sender_service-1           | 2024-07-25T16:51:02.821Z  INFO 1 --- [sender_service] [nio-8093-exec-2] c.e.s.s.impls.ClientDataServiceImpl      : Sending client data by id 3123
first_receiver_service-1   | 2024-07-25T16:51:02.827Z  WARN 1 --- [first_receiver_service] [ntContainer#0-1] o.s.a.s.c.Jackson2JsonMessageConverter   : Could not convert incoming message with content-type [text/plain], 'json' keyword missing.
first_receiver_service-1   | 2024-07-25T16:51:02.830Z  INFO 1 --- [first_receiver_service] [ntContainer#0-1] e.f.c.RabbitMQFirstReceiverConfiguration : Message read from first queue: 3123
first_receiver_service-1   | 2024-07-25T16:51:02.857Z  INFO 1 --- [first_receiver_service] [ntContainer#0-1] c.e.f.s.impls.FirstReceiverServiceImpl   : Updated client data sent to the queue secondQueue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe)
second_receiver_service-1  | 2024-07-25T16:51:02.892Z  INFO 1 --- [second_receiver_service] [ntContainer#0-1] .s.c.RabbitMQSecondReceiverConfiguration : Message read from second queue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe, email=null, phoneNumber=null, address=null)
second_receiver_service-1  | 2024-07-25T16:51:02.902Z  INFO 1 --- [second_receiver_service] [ntContainer#0-1] c.e.s.s.impls.SecondReceiverServiceImpl  : Updated client data sent to the queue thirdQueue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe, email=john.doe@example.com, phoneNumber=555-555-5555, address=123 Main Street)
third_receiver_service-1   | 2024-07-25T16:51:02.953Z  INFO 1 --- [third_receiver_service] [ntContainer#0-1] e.t.c.RabbitMQThirdReceiverConfiguration : Message read from third queue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe, email=john.doe@example.com, phoneNumber=555-555-5555, address=123 Main Street, cardNumbers=null, savedContacts=null)
third_receiver_service-1   | 2024-07-25T16:51:02.967Z  INFO 1 --- [third_receiver_service] [ntContainer#0-1] c.e.t.s.impls.ThirdReceiverServiceImpl   : Updated client data sent to the queue furthQueue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe, email=john.doe@example.com, phoneNumber=555-555-5555, address=123 Main Street, cardNumbers=[5555 3333 6666 8888, 2222 1111 4444 5555], savedContacts=[SavedContactsDTO(contactName=Valeria, contactNumber=555 888 333 444), SavedContactsDTO(contactName=Jack, contactNumber=666 777 444 333)])
data_storage_service-1     | 2024-07-25T16:51:02.979Z  INFO 1 --- [data_storage_service] [ntContainer#0-1] c.e.d.c.RabbitMQDataStorageConfiguration : Message read from furth queue: ClientDataDTO(clientId=3123, firstName=John, lastName=Doe, email=john.doe@example.com, phoneNumber=555-555-5555, address=123 Main Street, cardNumbers=[5555 3333 6666 8888, 2222 1111 4444 5555], savedContacts=[SavedContactsDTO(contactName=Valeria, contactNumber=555 888 333 444), SavedContactsDTO(contactName=Jack, contactNumber=666 777 444 333)])
```
- RabbitMQ overview data:
  ![Alt text](screenshots_for_github/rabbitmq_overview.PNG?raw=true "RabbitMQ overview")
- RabbitMQ exchanges:
  ![Alt text](screenshots_for_github/rabbitmq_exchanges.PNG?raw=true "RabbitMQ exchanges")
- RabbitMQ queses:
  ![Alt text](screenshots_for_github/rabbitmq_queues.PNG?raw=true "RabbitMQ queses")
## Performance Improvements

The following steps can be used to improve the performance of services:

- Retry Mechanism: Implement retry logic in case of network or broker issues using `spring-retry`.
- Circuit Breaker: Use Resilience4j to add circuit breaker patterns to handle failing external service calls gracefully.
- Bulkhead Pattern: Apply bulkhead isolation to prevent failures in one service from cascading to others.

### Example for Retry Mechanism
```java
@EnableRetry
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
```
### Example for Circuit Breaker
```java
@Configuration
public class Resilience4JConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }
}
```
This README.md file provides a comprehensive overview of the microservice architecture, including descriptions, configurations, Dockerfiles, Docker Compose setup, performance improvements, etc.
