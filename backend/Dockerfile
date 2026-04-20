# 构建阶段
FROM maven:3.8.1-jdk-8-slim AS builder
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段 - 使用精简 JRE 镜像
FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/yuoj-backedn-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8101
ENTRYPOINT ["java", \
  "-Xms128m", "-Xmx384m", \
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=100", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar", \
  "--spring.profiles.active=prod"]