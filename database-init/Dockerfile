# Etapa 1: compilación
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# Etapa 2: ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 10001 appuser

COPY --from=build --chown=10001:10001 /build/target/*.jar app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]