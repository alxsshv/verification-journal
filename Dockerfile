FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=./build/libs/verification-journal-v.1.0.0.jar
ARG FONT=./build/resources/main/fonts/GentiumBookPlus.ttf
WORKDIR /usr/src/app/
COPY ${JAR_FILE} /usr/src/app/verification-journal.jar
COPY ${FONT} /usr/src/app/GentiumBookPlus.ttf
ENTRYPOINT ["java","-jar","/usr/src/app/verification-journal.jar", "-Dspring.profiles.active=docker"]