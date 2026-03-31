# ====== BUILD STAGE ======
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar pom y descargar dependencias (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código
COPY src ./src

# Compilar
RUN mvn clean package -DskipTests

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar el jar generado
COPY --from=builder /build/target/*.jar app.jar

# Puerto
EXPOSE 8080

# Ejecutar app
ENTRYPOINT ["java","-jar","app.jar"]