# Stage 1: Build the application
FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the source code
COPY . .

# Build the JAR file
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar carrental.jar
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "carrental.jar"]
