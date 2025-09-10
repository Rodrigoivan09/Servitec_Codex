# Multi-stage build for Servitec (Spring Boot)

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
# Build without tests to speed up container builds
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=UTC
COPY --from=build /workspace/target/*.jar /app/app.jar

# Create non-root user and take ownership
RUN useradd --create-home --uid 10001 --shell /usr/sbin/nologin appuser \
    && chown -R appuser:appuser /app

# Expose the application port
EXPOSE 8090

# Default JVM options can be overridden via JAVA_TOOL_OPTIONS
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# Drop privileges
USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]
