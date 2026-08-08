FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl platform/api-gateway -am -DskipTests && rm -f platform/api-gateway/target/*.original

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/platform/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
