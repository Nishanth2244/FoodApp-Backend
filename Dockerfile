# ---------- BUILD STAGE ----------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

# download dependencies (FASTER builds)
RUN ./mvnw dependency:go-offline -B

# copy source
COPY src src

# build jar
RUN ./mvnw clean package -DskipTests


# ---------- RUN STAGE ----------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# copy jar (any version)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
