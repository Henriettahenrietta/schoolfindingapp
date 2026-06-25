# ---- Build stage ----
# Uses the Gradle image so no local JDK/Gradle install is required.
FROM gradle:8.10-jdk21 AS build
WORKDIR /home/gradle/project
# Cache dependencies first
COPY settings.gradle.kts build.gradle.kts ./
RUN gradle --no-daemon dependencies > /dev/null 2>&1 || true
# Build the app
COPY src ./src
RUN gradle --no-daemon clean bootJar

# ---- Run stage ----
FROM eclipse-temurin:21-jre AS run
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
