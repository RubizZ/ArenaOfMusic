# Build Stage: Use Maven JDK image (Maven 4.0.0)
FROM maven:4.0.0-jdk-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml ./
COPY src ./src

# Build the application using Maven 4.0.0
RUN mvn clean package -DskipTests

# Stage 2: Final image based on OpenJDK 19
FROM maven:4.0.0-jdk-21

# Set the working directory in the container
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port that your Spring Boot application will run on
EXPOSE 8080

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
