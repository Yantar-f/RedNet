FROM maven:3.9.0-amazoncorretto-17 as build
WORKDIR /app
COPY . .
RUN mvn package
EXPOSE 8080
CMD ["java", "-jar", "/app/target/red-net.jar"]