FROM openjdk:11-slim as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw install

FROM openjdk:11-slim
EXPOSE 9000
COPY --from=build /workspace/app/target/fhir-converter-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "fhir-converter-0.0.1-SNAPSHOT.jar"]



