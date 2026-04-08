# ---- Build stage ----
FROM maven:3.9.14-eclipse-temurin-21 AS builder
WORKDIR /app

ENV MAVEN_OPTS="-Daether.connector.https.securityMode=insecure \
                -Dmaven.wagon.http.ssl.insecure=true \
                -Dmaven.wagon.http.ssl.allowall=true"

# Copy all POMs first (layer caching)
COPY pom.xml .
COPY core-security-starter/pom.xml core-security-starter/
COPY sample-application/pom.xml sample-application/

RUN mvn -B dependency:go-offline -pl sample-application -am

# Copy sources
COPY core-security-starter/src core-security-starter/src
COPY sample-application/src sample-application/src

RUN mvn clean package -DskipTests -pl sample-application -am

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser
COPY --from=builder /app/sample-application/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]







