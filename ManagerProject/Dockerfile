FROM gradle:8.12.1-jdk17-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build -x test

CMD ["java", "-jar", "./build/libs/ManagerProject-1.0-SNAPSHOT.jar"]