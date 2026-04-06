FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

RUN ./gradlew installDist -x test

CMD ["./build/install/hexlet-cv/bin/hexlet-cv"]