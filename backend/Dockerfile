# JDK 18镜像
FROM openjdk:18-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装必要的工具和配置国内镜像源
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

# 复制Maven配置文件和源代码
COPY pom.xml .
COPY src ./src

# 下载并安装Maven
RUN wget https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz \
    && tar -xzf apache-maven-3.8.8-bin.tar.gz \
    && mv apache-maven-3.8.8 /opt/maven \
    && rm apache-maven-3.8.8-bin.tar.gz

# 设置Maven环境变量
ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

# 编译项目
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 运行应用 - 使用正确的jar文件名
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/tracing-1.0.0.jar"] 