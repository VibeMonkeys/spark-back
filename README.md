# ✨ Spark Backend API

랜덤 미션 기반의 소셜 서비스, Spark의 백엔드 API입니다.

## 📖 프로젝트 소개

Spark는 사용자들이 랜덤으로 부여된 미션을 수행하고 공유하며 소통하는 서비스입니다. 이 프로젝트는 Spark의 핵심 비즈니스 로직, 데이터 관리, 사용자 인증 등을 처리하는 백엔드 서버입니다.

- **주요 기능:**
  - 사용자 인증 (JWT 기반, 소셜 로그인 포함)
  - 랜덤 미션 제공 및 관리
  - 미션 수행 결과 기록 및 공유
  - 실시간 알림 (WebSocket)

## 🛠️ 기술 스택

- **언어:** `Kotlin 1.9.25`
- **프레임워크:** `Spring Boot 3.5.4`
- **데이터베이스:** `PostgreSQL`, `H2` (로컬)
- **ORM:** `Spring Data JPA`
- **인증:** `Spring Security`, `JWT`
- **빌드 도구:** `Gradle`
- **컨테이너:** `Docker`

## ⚙️ 실행 방법

### 1. 사전 준비

- `JDK 21` 이상 설치
- `Docker` 및 `Docker Compose` 설치

### 2. 데이터베이스 설정 (PostgreSQL)

프로젝트 루트(`spark-back`)에서 아래 명령어를 실행하여 PostgreSQL 데이터베이스를 Docker 컨테이너로 실행합니다.

```bash
docker-compose up -d
```

- **DB 정보:**
  - **Host:** `localhost`
  - **Port:** `5432`
  - **Database:** `spark`
  - **Username:** `root`
  - **Password:** `1234`

### 3. 환경 변수 설정

`src/main/resources/` 경로에 `application-dev.yml` 파일을 생성하고, `docker-compose.yml`과 연동되는 DB 정보 및 JWT 시크릿 키를 설정합니다.

```yaml
# src/main/resources/application-dev.yml

spring:
  config:
    activate:
      on-profile: dev

  datasource:
    url: jdbc:postgresql://localhost:5432/spark
    username: root
    password: 1234
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: create # 또는 update
    properties:
      hibernate:
        show_sql: true
        format_sql: true

jwt:
  secret:
    key: "your-super-secret-jwt-key-that-is-long-enough" # 실제 프로덕션에서는 훨씬 더 강력한 키를 사용해야 합니다.
```

### 4. 애플리케이션 실행

프로젝트 루트(`spark-back`)에서 아래 Gradle 명령어를 실행하여 `dev` 프로필로 애플리케이션을 시작합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

서버가 정상적으로 실행되면 `http://localhost:8099` 에서 API를 사용할 수 있습니다.

## 🧪 테스트

아래 명령어를 실행하여 전체 테스트를 수행할 수 있습니다.

```bash
./gradlew test
```

## 🐳 Docker 빌드 및 실행

프로젝트의 `Dockerfile`을 사용하여 애플리케이션을 컨테이너화할 수 있습니다.

1.  **Docker 이미지 빌드:**

    ```bash
    docker build -t spark-backend .
    ```

2.  **Docker 컨테이너 실행:**

    ```bash
    docker run -p 8080:8080 \
      -e SPRING_PROFILES_ACTIVE=production \
      -e DB_URL=<your_production_db_url> \
      -e DB_USERNAME=<your_db_username> \
      -e DB_PASSWORD=<your_db_password> \
      -e JWT_SECRET_KEY=<your_production_jwt_secret> \
      spark-backend
    ```

    > **참고:** `production` 프로필 실행 시에는 실제 DB 정보와 JWT 키를 환경 변수로 전달해야 합니다.

## 🚀 배포

이 프로젝트는 `Railway`를 통해 배포할 수 있도록 설정되어 있습니다. 자세한 내용은 [RAILWAY_DEPLOYMENT.md](RAILWAY_DEPLOYMENT.md) 문서를 참고하세요.

```