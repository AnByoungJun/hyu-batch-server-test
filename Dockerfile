# 런타임 전용 이미지. jar 는 CI(Jenkins)에서 `gradlew bootJar` 로 먼저 빌드된 뒤 COPY 된다.
#   로컬에서 직접 빌드할 때도 먼저 `./gradlew clean bootJar -x test` 실행 필요.
# 환경중립: 프로파일/비밀은 이미지에 굽지 않고 실행 시 주입한다.
#   - 프로파일: 실행 시 SPRING_PROFILES_ACTIVE 지정 (미지정 시 datasource 없어 기동 실패)
#   - 비밀:    DB_USERNAME/DB_PASSWORD 등 (compose env_file / -e / Secret)
FROM eclipse-temurin:21-jre

# Timezone (Asia/Seoul) + healthcheck 용 curl 설치
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY build/libs/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
