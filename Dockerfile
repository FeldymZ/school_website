# ===============================
# BUILD STAGE
# ===============================
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle gradle
COPY src src

RUN gradle bootJar --no-daemon

# ===============================
# RUN STAGE
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# 🔥 (OPTIONNEL MAIS RECOMMANDÉ POUR PDF)
RUN apt-get update && apt-get install -y fontconfig && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]