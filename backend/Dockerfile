# JDK 18镜像
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:18-bullseye

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY src ./src

# 安装Maven
RUN apt-get update && apt-get install -y maven

# 编译项目
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 运行应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/rumor-tracing-1.0.0.jar"] 