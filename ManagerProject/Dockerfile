FROM gradle:7.4.1-jdk17 AS builder

WORKDIR /app

COPY ./ManagerProject .

RUN ./gradlew clean build -x test

CMD ["java", "-jar", "./build/libs/ManagerProject-1.0-SNAPSHOT.jar"]