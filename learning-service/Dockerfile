FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/learning-service-0.0.1-SNAPSHOT.war learning-service.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","learning-service.war"]
