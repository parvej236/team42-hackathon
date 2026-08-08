FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl platform/discovery-server -am -DskipTests && rm -f platform/discovery-server/target/*.original

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/platform/discovery-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
