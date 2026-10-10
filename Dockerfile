# ==========================================
# Build Stage
# ==========================================
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy Maven Wrapper and POM file
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Make wrapper executable
RUN chmod +x ./mvnw

# Copy source code and package application
COPY src src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Run Stage
# ==========================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Non-root user for cloud security best practices
RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN mkdir -p /app/data && chown -R spring:spring /app/data
USER spring:spring

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Dynamic port environment
ENV PORT=8080
EXPOSE 8080

# Run with memory container support enabled
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
