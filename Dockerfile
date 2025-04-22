# Build Stage: Use OpenJDK 19 to build the app
FROM openjdk:21-jdk AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml ./
COPY src ./src

# Install Maven (if not already available)
RUN apt-get update && apt-get install -y maven

# Build the application using Maven
RUN mvn clean package -DskipTests

# Stage 2: Final image based on OpenJDK 19
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port that your Spring Boot application will run on
EXPOSE 8080

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
