# syntax=docker/dockerfile:1

# ---- build ----
# Cada servico e um projeto Gradle independente, por isso o contexto de build
# e a propria pasta do servico.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src src
# A cache do Gradle e partilhada por todos os servicos: a distribuicao do
# wrapper e as dependencias sao descarregadas uma vez so, nao seis.
RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    chmod +x gradlew && ./gradlew bootJar --no-daemon

# ---- runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/build/libs/*.jar app.jar
USER app
EXPOSE 8079
ENTRYPOINT ["java", "-jar", "app.jar"]
