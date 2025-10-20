FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/application.jar
EXPOSE 8080
RUN apk update && apk add --no-cache curl
CMD ["java", "-Dserver.port=${PORT:-8080}", "-jar", "/app/application.jar"]
