FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/payment-service-1.0.0.jar app.jar
EXPOSE 8881
ENTRYPOINT ["java", "-jar", "app.jar"]
