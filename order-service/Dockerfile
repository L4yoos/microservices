FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/order-service-1.0.0.jar app.jar
EXPOSE 8880
ENTRYPOINT ["java", "-jar", "app.jar"]