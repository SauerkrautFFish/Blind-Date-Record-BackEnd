# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-alpine

# 为应用程序创建目录
WORKDIR /app

# 复制构建后的JAR文件到容器中
COPY target/main.jar /app/main.jar

# 暴露应用程序的端口（如果Spring Boot默认端口8080被使用）
EXPOSE 8080

# 启动Spring Boot应用程序
ENTRYPOINT ["java", "-jar", "main.jar", "--spring.profiles.active=prod"]