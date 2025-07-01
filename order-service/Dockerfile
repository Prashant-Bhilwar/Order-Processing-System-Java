FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl order-service -am

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]