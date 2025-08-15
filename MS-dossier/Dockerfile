FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY MS-deal/pom.xml MS-deal/
COPY Gateway/pom.xml Gateway/
COPY MS-calculator/pom.xml MS-calculator/
COPY MS-dossier/pom.xml MS-dossier/
COPY MS-statement/pom.xml MS-statement/

RUN mvn dependency:go-offline -B

COPY . .

RUN mvn clean package -pl MS-dossier -am -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/MS-dossier/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
