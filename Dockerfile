FROM gradle:jdk19

WORKDIR /app

COPY . .

RUN gradle shadowJar

FROM openjdk:19-alpine

WORKDIR /app

COPY --from=0 /app/build/libs/*.jar .

CMD ["java", "-jar", "app-1.0-SNAPSHOT-all.jar"]

