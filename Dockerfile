FROM openjdk:21-jdk-slim AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y el código fuente
COPY pom.xml ./
COPY src ./src

# Instalar Maven y compilar la aplicación
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Etapa final: Usa OpenJDK 21 para la imagen final
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar /app/app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Cambiar el ENTRYPOINT para usar el perfil 'render'
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=render"]
