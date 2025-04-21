FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /workspace

COPY pom.xml .
# 首先只下载依赖，利用 Docker 缓存
RUN mvn dependency:go-offline

COPY src src
COPY config config

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
VOLUME /workspace/logs
VOLUME /workspace/config
WORKDIR /app

COPY --from=builder /workspace/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/workspace/config/"] 