FROM openjdk:11-slim as build
WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY src src
RUN ./gradlew build


FROM openjdk:11-slim
EXPOSE 9000
COPY --from=build /workspace/app/build/libs/app-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]