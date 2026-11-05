# ── Etapa 1: compilacion con Maven ──────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# pom.xml primero: aprovecha la cache de capas si las dependencias no cambian
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Etapa 2: imagen de ejecucion (solo JRE) ─────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no root: buena practica de seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

