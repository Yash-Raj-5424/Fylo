FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY backend/pom.xml backend/
COPY backend/src backend/src/
RUN mvn clean package -DskipTests -pl backend -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/target/backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV FYLO_UPLOAD_DIR=/data/uploads
CMD ["java", "-jar", "app.jar"]
