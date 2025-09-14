# Build stage

FROM bellsoft/liberica-openjdk-alpine:23 AS builder

WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test


# Run stage

FROM bellsoft/liberica-openjdk-alpine:23

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY keys/private.pem keys/private.pem
COPY keys/public.pem keys/public.pem

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]