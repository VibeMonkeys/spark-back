# 멀티 스테이지 빌드로 최적화
FROM gradle:8.5-jdk21-jammy AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 최적화를 위해 의존성 파일들 먼저 복사
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./
COPY gradle/ gradle/

# 의존성 다운로드 (캐시 레이어)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드 (테스트 제외)
RUN gradle build -x test --no-daemon

# 런타임 이미지
FROM eclipse-temurin:21-jre-jammy

# 타임존 설정 (선택사항)
ENV TZ=Asia/Seoul

# 애플리케이션 실행을 위한 사용자 생성 (보안)
RUN useradd -m -s /bin/bash appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 파일 소유권 변경
RUN chown appuser:appuser app.jar

# 애플리케이션 사용자로 전환
USER appuser

# Railway에서 동적으로 할당하는 포트 노출
EXPOSE $PORT

# JVM 최적화 옵션
ENV JAVA_OPTS="-Xmx400m -Xms200m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 애플리케이션 실행
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]