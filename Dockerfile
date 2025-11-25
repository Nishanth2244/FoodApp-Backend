FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# copy wrapper + pom first (cache optimization)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# make wrapper executable
RUN chmod +x ./mvnw

# copy source
COPY src ./src

# build
RUN ./mvnw -q -DskipTests package

EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]
