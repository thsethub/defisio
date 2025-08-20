FROM eclipse-temurin:17-jdk-jammy
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests
ENTRYPOINT [ "java" , "-jar", "target/defisio-0.0.1-SNAPSHOT.jar"]