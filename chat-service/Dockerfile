FROM amazoncorretto:17 as build
WORKDIR /app
COPY target .
EXPOSE 8082
CMD ["java", "-jar", "/app/red-net-chat-service.jar"]