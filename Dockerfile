# ────────────────────── Стадия сборки ──────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Копируем Maven Wrapper и POM-файл
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Загружаем зависимости отдельно (ускоряет билды)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Копируем исходный код приложения
COPY src src

# Собираем fat-jar (JAR с зависимостями)
RUN ./mvnw clean package -DskipTests -B

# ────────────────────── Стадия запуска ──────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копируем собранный JAR-файл из стадии build
COPY --from=build /app/target/*.jar app.jar

# Настройки профиля
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseSerialGC -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8 -Dspring.main.lazy-initialization=true"

# Открываем порт 8080
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
