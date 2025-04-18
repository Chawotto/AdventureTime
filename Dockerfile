# ───────────────────────────────────────────────────────────
# СТАДИЯ 1: Сборка приложения
# ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

# Рабочая папка внутри контейнера
WORKDIR /app

# Копируем Maven wrapper и файл pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Разрешаем выполнение mvnw и загружаем зависимости
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Копируем исходный код проекта
COPY src src

# Собираем JAR-файл
RUN ./mvnw clean package -DskipTests -B

# ───────────────────────────────────────────────────────────
# СТАДИЯ 2: Запуск приложения
# ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Рабочая папка внутри контейнера
WORKDIR /app

# Копируем собранный JAR из предыдущей стадии
COPY --from=build /app/target/*.jar app.jar

# Устанавливаем активный Spring-профиль по умолчанию
ENV SPRING_PROFILES_ACTIVE=prod

# Открываем порт 8080
EXPOSE 8080

# Стандартная команда запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
