# 빌드 단계 - Java 17 JDK를 사용
FROM gradle:7.5.1-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 활용
COPY build.gradle settings.gradle ./
RUN gradle build -x test --no-daemon || return 0

# 프로젝트 파일 복사 및 빌드 실행
COPY . .
RUN gradle bootJar -x test --no-daemon

# 실행 단계 - 더 작은 이미지로 빌드 결과만 복사
FROM eclipse-temurin:17-jre
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 설정
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]

ENV DEV_SERVER_PORT=8080
