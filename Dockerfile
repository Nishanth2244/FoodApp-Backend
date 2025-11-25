# Use Java 21 base image

FROM eclipse-temurin:21-jdk-alpine
 
# Set working directory

WORKDIR /app
 
# Copy project files

COPY . .
 
# Build the Spring Boot app (skip tests)

RUN ./mvnw -q -DskipTests package
 
# Expose port

EXPOSE 8080
 
# Start the application

CMD ["java", "-jar", "target/*.jar"]

 