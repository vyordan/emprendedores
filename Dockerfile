# ══════════════════════════════════════════════
# ETAPA 1 — Build con Maven
# ══════════════════════════════════════════════
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar pom primero para aprovechar cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el resto del código y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ══════════════════════════════════════════════
# ETAPA 2 — Imagen final solo con el JAR
# ══════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario no root por seguridad
RUN addgroup -S chamba && adduser -S chamba -G chamba
USER chamba

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]