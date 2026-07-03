# ETAPA 1: Construcción (Multi-stage build)
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
WORKDIR /app
# Compilamos el proyecto omitiendo los tests para ir rápido
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM openjdk:17-jdk-slim
# Copiamos solo el jar generado de la etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]