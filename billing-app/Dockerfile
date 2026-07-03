# ETAPA 1: Construcción
# Usamos maven con eclipse-temurin (el reemplazo oficial de openjdk)
FROM maven:3.8.5-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
# Compilamos el proyecto
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
# Usamos la imagen JRE (Runtime) de eclipse-temurin que es más ligera y segura
FROM eclipse-temurin:17-jre-jammy
# Copiamos el jar generado
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]