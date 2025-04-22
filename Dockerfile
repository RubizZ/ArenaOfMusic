FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN apt-get update && apt-get install -y netcat
RUN apt-get update && apt-get install -y maven

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "-c", "\
  echo 'Creando base de datos con perfil default...' && \
  java -jar /app/app.jar --spring.profiles.active=default & \
  pid=$! && \
  echo 'Esperando a que la app esté disponible en el puerto 8080...' && \
  while ! nc -z localhost 8080; do sleep 1; done && \
  echo 'Base de datos creada. Matando proceso...' && \
  kill $pid && \
  sleep 2 && \
  echo 'Arrancando en perfil container...' && \
  exec java -jar /app/app.jar --spring.profiles.active=container \
"]
