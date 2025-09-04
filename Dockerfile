# Build stages
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /money-manager
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#Run stage
FROM amazoncorretto:21
WORKDIR /money-manager
COPY --from=build /money-manager/target/money-manager-0.0.1-SNAPSHOT.jar money-manager.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "money-manager.jar"]