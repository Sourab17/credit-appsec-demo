FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S  appuser -G appgroup
WORKDIR /app
COPY target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]