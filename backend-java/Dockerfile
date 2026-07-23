ARG MAVEN_IMAGE=maven:3.9.9-eclipse-temurin-17
ARG RUNTIME_IMAGE=eclipse-temurin:17-jre
ARG MAVEN_MIRROR_URL=https://maven.aliyun.com/repository/public

FROM ${RUNTIME_IMAGE} AS local-jar

WORKDIR /app

COPY target/practice-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

FROM ${MAVEN_IMAGE} AS build

WORKDIR /app

ARG MAVEN_MIRROR_URL

COPY pom.xml ./
RUN mkdir -p /root/.m2 \
    && printf '%s\n' \
      '<?xml version="1.0" encoding="UTF-8"?>' \
      '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' \
      '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
      '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
      '  <mirrors>' \
      '    <mirror>' \
      '      <id>central-mirror</id>' \
      '      <mirrorOf>*</mirrorOf>' \
      "      <url>${MAVEN_MIRROR_URL}</url>" \
      '    </mirror>' \
      '  </mirrors>' \
      '</settings>' > /root/.m2/settings.xml \
    && mvn -B -s /root/.m2/settings.xml -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -s /root/.m2/settings.xml -DskipTests package

FROM ${RUNTIME_IMAGE} AS packaged

WORKDIR /app

COPY --from=build /app/target/practice-0.0.1-SNAPSHOT.jar /app/app.jar

RUN mkdir -p /app/import-storage

EXPOSE 8080

ENV JAVA_OPTS=""
ENV IMPORT_STORAGE_ROOT=/app/import-storage

VOLUME ["/app/import-storage"]

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
