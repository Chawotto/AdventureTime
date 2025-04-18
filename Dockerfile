# ─── 1. Build stage ───────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Копируем Maven wrapper и pom.xml, чтобы закешировать зависимости
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline -B

# Копируем исходники и собираем JAR
COPY src src
RUN ./mvnw package -DskipTests -B

# ─── 2. Run stage ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копируем собранный JAR из build-стадии
COPY --from=build /app/target/*.jar app.jar

# По умолчанию — продакшен‑профиль
ENV SPRING_PROFILES_ACTIVE=prod

# Команда запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
