FROM eclipse-temurin:25-jdk AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
