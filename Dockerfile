FROM eclipse-temurin:17-jdk-jammy

LABEL authors="Rustam"
LABEL version="1.0"
LABEL description="User Subscription Service"

WORKDIR /app

COPY target/user-subscription-service-*.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENV SERVER_PORT=8082

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --server.port=${SERVER_PORT}"]