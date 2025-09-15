FROM maven:3-openjdk-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/englishApp-0.0.1-SNAPSHOT.war englishApp.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","englishApp.war"]