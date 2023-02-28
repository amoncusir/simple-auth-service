# Build phase

FROM gradle:7-jdk11 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew buildFatJar --no-daemon

# Run app

FROM amazoncorretto:11-alpine3.17-jdk

ENV PORT 80

EXPOSE 80:80

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/fat.jar /app/auth.jar

ENTRYPOINT ["java","-jar","/app/auth.jar"]